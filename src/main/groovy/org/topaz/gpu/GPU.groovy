package org.topaz.gpu

import org.topaz.gpu.LCD
import org.topaz.MemoryManager
import org.topaz.InterruptHandler
import org.topaz.util.BitUtil
import org.topaz.ui.Display
import java.lang.Math

class GPU{
    static final int CYCLES_BETWEEN_SCANLINES = 456

    /*
     * It takes 456 CPU cycles to draw each scanline. In the main emulator loop
     * the graphics is updated after the execution of each opcode and the total
     * number of CPU cycles executed thus far is subtracted from
     * scanLineCounter. If the result is 0 or less, it is time to draw the next
     * scanline.
     */
    static int SCAN_LINE_CYCLES_COUNTER = 0

    /*
     * The screen resolution is 160x144, however, the Gameboy actually draws 153
     * scanlines instead of 144. The extra 8 scanlines are invisible and
     * constitute the vertical blank period (i.e., between the 144th and 153rd
     * scanline).
     */
    static final int V_BLANK_SCANLINE_START = 144
    static final int V_BLANK_SCANLINE_END = 153

    private final int TILE_PIXEL_WIDTH = 8
    private final int TILE_PIXEL_HEIGHT = 8
    private final int SIZE_OF_TILE_IN_MEMORY = TILE_PIXEL_HEIGHT + TILE_PIXEL_WIDTH

    /*
     * The tile data begins at either of these locations. The sprite tiles always 
     * begin at location 1.
     */
    private final int TILE_DATA_LOCATION_1 = 0x8000
    private final int TILE_DATA_LOCATION_2 = 0x8800

    private MemoryManager memoryManager
    private InterruptHandler interruptHandler
    private LCD lcd
    private Display display
    private int[][][] screenData

    enum Colour{
        WHITE,
        LIGHT_GRAY,
        DARK_GRAY,
        BLACK
    }

    public GPU(MemoryManager memoryManager, InterruptHandler interruptHandler, Display display) {
        this.memoryManager = memoryManager
        this.interruptHandler = interruptHandler
        this.display = display
        this.screenData = new int[LCD.WIDTH][LCD.HEIGHT][3]
        lcd = new LCD(memoryManager: memoryManager, interruptHandler: interruptHandler)
    }

    public void updateGraphics(int cycles) {
        lcd.setLCDStatus()

        if(lcd.isLCDEnabled()) {
            SCAN_LINE_CYCLES_COUNTER = SCAN_LINE_CYCLES_COUNTER - cycles
        }else {
            return
        }
        if(SCAN_LINE_CYCLES_COUNTER <= 0) {
            /*
             * Move to next scanline. Memory is accessed directly instead of using
             * the writeMemory method of the memory manager. This is because any
             * writes to 0xFF44 (CURRENT_SCANLINE) must reset the current scanline
             * to 0. Which is what the memory manager is programmed to do.
             */
            memoryManager.rom[LCD.LY_REGISTER]++
            int currentLine = memoryManager.readMemory(LCD.LY_REGISTER)

            /* Reset the scanline counter */
            SCAN_LINE_CYCLES_COUNTER = CYCLES_BETWEEN_SCANLINES

            if(currentLine == V_BLANK_SCANLINE_START) {
                /* Request the appropriate interrupt if in a vertical blank period */
                interruptHandler.requestInterrupt(InterruptHandler.V_BLANK_INTERRUPT)
            }else if(currentLine > V_BLANK_SCANLINE_END) {
                /*
                 * If we are beyond the vertical blank period, the current
                 * scanline must then be reset.
                 */
                memoryManager.rom[LCD.LY_REGISTER] = 0
            }else if(currentLine < V_BLANK_SCANLINE_START) {
                drawScanLine()
            }
        }
    }

    private void drawScanLine() {
        /*
         * The Gameboy does not have a direct frame buffer and instead uses
         * a tiling system. Tiles are 8x8 pixels that are placed on the screen
         * at a certain index. Two tile map lists are maintained for the
         * background and window as this allows the game to quickly switch
         * between tile maps without any loading.
         */
        int control = memoryManager.readMemory(LCD.LCDC_REGISTER)

        if(BitUtil.isSet(control, LCD.ControlRegisterBit.BG_DISPLAY)) {
            renderTiles()
        }

        if(BitUtil.isSet(control, LCD.ControlRegisterBit.OBJ_SPRITE_DISPLAY_ENABLE)) {
            renderSprites()
        }
    }

    public updateDisplay() {
        display.update(screenData)
    }

    private void renderTiles() {
        /*
         * SCROLL_X and SCROLL_Y specify the position in the 256x256 background
         * (BG) map from which to start drawing the background. This is necessary
         * since the game only ever displays 160x144 pixels of the available
         * 256x256 pixels. This allows for a scrolling background, but makes
         * knowing a starting point from which to draw necessary.
         */
        final int SCROLL_Y = memoryManager.readMemory(0xFF42)
        final int SCROLL_X = memoryManager.readMemory(0xFF43)

        /*
         * WINDOW_X and WINDOW_Y specify the start position within the background
         * from which the window must be drawn. The window is an alternate
         * background area that is displayed above the normal background. The
         * value for WINDOW_X must be reduced by 7 so that the window is drawn in
         * the correct location. Unlike the background, this window does not
         * scroll. It is useful for things like displaying in game score, lives
         * remaining, etc.
         */
        final int WINDOW_Y = memoryManager.readMemory(0xFF4A)
        final int WINDOW_X = memoryManager.readMemory(0xFF4B)

        //     +----------------------------------------------------------->256px
        //     |                                   ^
        //     |                                   |
        //     |                                   |
        //     |                                SCROLL_Y
        //     |                                   |
        //     |                                   |
        //     |                                   V               160px
        //     |                +---------------------------------------+
        //     |          144px | background                 ^          |
        //     |                |                            |          |
        //     |                |                         WINDOW_Y      |
        //     |                |                            |          |
        //     |<---SCROLL_X--->|                            V          |
        //     |                |                   +---------------+   |
        //     |                |                   |               |   |
        //     |                | <----WINDOW_X---->|     window    |   |
        //     |                |                   +---------------+   |
        //     |                +---------------------------------------+
        //     |
        //     |
        //     V
        //    256px

        /*
         * The scroll and window coordinates respectively let us know where the
         * background and window should be drawn. Tiles are set of small bitmaps
         * held in memory. And both tile maps are constructed by referencing
         * specific tiles from the set of all tiles. The tile set is stored in
         * two memory regions (0x8800 - 0x97FF and 0x8000 - 0x8FFF) and consists
         * of tiles used for rendering both the background and the window in the
         * tile maps. The list of tile indexes for creating the two tile maps are
         * respectively indicated by bits 3 and 6 of the LCD control (LCDC)
         * register. If the bits are set to 0 then the list of tile indexes must
         * be read from memory region 0x9800 - 0x9BFF, otherwise, the list at
         * region 0x9C00 - 0x9FFF must be used. Each byte in the list is a tile
         * identification number of what needs to be drawn.
         */

        int tileData = 0
        int memoryRegion = 0
        boolean isUnsigned = true
        boolean usingWindow = false

        if(BitUtil.isSet(LCD.LCDC_REGISTER, LCD.ControlRegisterBit.WINDOW_DISPLAY_ENABLE)) {
            /*
             * Is the current scanline being drawn within the windows Y position?
             * This is later needed for determining which memory region
             * (background or window) the tile should be loaded from. 
             */
            if(WINDOW_Y <= memoryManager.readMemory(LCD.LY_REGISTER)) {
                usingWindow = true
            }

            /*
             * The tile data is stored in two memory regions, 0x8800 - 0x97FF and
             * 0x8000 - 0x8FFF. Bit 4 of the LCD Control register indicates
             * which region should be used for loading the tile data. If this bit
             * is 0, memory region 0x8800 - 0x97FF is used, otherwise region
             * 0x8000 - 0x8FFF is used.
             */
            if(BitUtil.isSet(LCD.LCDC_REGISTER, LCD.ControlRegisterBit.BG_AND_WINDOW_TILE_DATA_SELECT)) {
                tileData = TILE_DATA_LOCATION_1
            }else {
                tileData = TILE_DATA_LOCATION_2
                /*
                 * While the Gameboy seems to largely use unsigned bytes, there
                 * is a quirk when it comes to tiles. As it turns out, if the
                 * tile data must be read from memory region 0x8000 - 0x97FF,
                 * then the tile identifier used has the layout of a signed byte
                 * and ranges from -127 (an invalid index) to 127. The signed
                 * nature of the byte needs to be accounted for when indexing the
                 * tile.
                 */
                isUnsigned = false
            }

            if(usingWindow == false) {
                /*
                 * If the current scanline is not being drawn in the window, get
                 * the correct memory region to load the background tile indexes
                 * from.
                 */
                if(BitUtil.isSet(LCD.LCDC_REGISTER, LCD.ControlRegisterBit.BG_TILE_MAP_DISPLAY_SELECT)) {
                    memoryRegion = 0x9C00
                }else {
                    memoryRegion = 0x9800
                }
            }else {
                /*
                 * Otherwise, load the window's list of of tile indexes from the
                 * specified memory region.
                 */
                if(BitUtil.isSet(LCD.LCDC_REGISTER, LCD.ControlRegisterBit.WINDOW_TILE_MAP_DISPLAY_SELECT)) {
                    memoryRegion = 0x9C00
                }else {
                    memoryRegion = 0x9800
                }
            }

            /*
             * The yPosition is used to calculate which of the 32 vertical tiles
             * the current scanline is drawing. Tiles can belong to either the
             * background or the non-scrolling window drawn above the background.
             */
            int yPosition = 0

            if(usingWindow == false) {
                /*
                 * If the current scanline is currently drawing the background,
                 * then the yPosition calculated is with respect to the
                 * background's Y location within the 256x256 screen.
                 */
                yPosition = SCROLL_Y + memoryManager.readMemory(LCD.LY_REGISTER)
            }else {
                /*
                 * If the current scanline is currently drawing the window, then
                 * the yPosition calculated is with respect to the window's Y
                 * location within the background.
                 */
                yPosition = memoryManager.readMemory(LCD.LY_REGISTER) - WINDOW_Y
            }

            final int PIXEL_ROWS_PER_TILE = 8
            final int TILES_PER_ROW = 32

            /*
             * The background is free to scroll around to any 160x144 pixels of
             * the available 256x256 pixels. To programmatically know the unique
             * row of pixels for rendering, a calculation is performed. The
             * yPosition represents the scanline's absolute position in the
             * 256x256 grid. The correct row of tiles that the scanline is within
             * is found by first dividing the yPosition by the number of pixel
             * rows per tile. We are only interested in the integer result of
             * this calculation. The result is then multiplied by the number of
             * tiles per row. The final result is the starting position of the
             * row in which the current scanline is within the 32x32 tile grid.
             * 
             * As an example:
             * Assume that the background (of size 160x144 pixels) is at the
             * upper left position of the 256x256 available pixels. This means
             * that SCROLL_Y and SCROLL_X will be 0 (the background's offset is 0
             * from each axis as it is flush against it). Next, assume that the
             * current scanline is at position 42. The yPosition's calculation is
             * SCROLL_Y + scanline, since SCROLL_Y is 0 this gives us yPosition =
             * 0 + 42. To get the correct row index, the yPosition is first
             * divided by the number of pixel rows per tile (each tile is 8x8
             * pixels). This gives 42/8 = 5 (remember we are only interested in
             * the integer part of the result). In the 32x32 grid of tiles, the
             * value 5 represents the 5th row of tiles. To get the index value of
             * the starting tile of the 5th row, it is multiplied by the number
             * of tiles per row (32) thus giving the tile row's starting index
             * value 5*32 = 160. The value 160 is the value at which the tile row
             * of the scanline *starts*. Remember that the background can be
             * offset from that starting position anywhere along the 32x32 tile
             * grid. The calculation of the xPosition gives the correct tile at
             * which the background begins.
             */

            int tileRowStart = ((yPosition / PIXEL_ROWS_PER_TILE) as int) * TILES_PER_ROW

            LCD.WIDTH.times {pixel->
                int xPosition = pixel + SCROLL_X

                /*
                 * Translate the current x position to the window if a window is
                 * being drawn.
                 */
                if(usingWindow) {
                    if(pixel >= WINDOW_X) {
                        xPosition = pixel - WINDOW_X
                    }
                }

                /*
                 * This calculation determines which of the 32 tiles along the
                 * x-axis on the 256x256 grid of tiles the current pixel's
                 * xPosition falls within, i.e., which tile column.
                 * 
                 * For example:
                 * For simplicity, assume that the background aligned to the
                 * upper left of the 256x256 pixels such that SCROLL_X and
                 * SCROLL_Y are both 0. Pixel 0 of the scanline would be 0/8 = 0.
                 * This means that the pixel falls in the first line of the tile
                 * (again, we are only interested in the integer result). Pixels
                 * 0 to 7 would give a result of zero. However, when we're at
                 * pixel 8 of the scanline the values become 8/8 = 1, thus
                 * indicating that the pixel is in the second tile of the column.
                 * Again this continues for pixels 8 to 15. And in much the same
                 * way, pixels 16 to 23 will give a result of 2, etc.
                 *
                 */
                int tileColumn = (xPosition / TILE_PIXEL_WIDTH) as int
                int tileId = 0

                /*
                 * The address of the tile to read from memory is calculated as
                 * follows: tileRow + tileColumn represent the offset into the
                 * selected memoryRegion that holds the list of all tiles. Adding
                 * their sum to the memoryRegion therefore gives the tile's
                 * address.
                 */
                int tileAddress = memoryRegion + tileRowStart + tileColumn

                int tileNumber = memoryManager.readMemory(tileAddress)

                int tileLocation = tileData

                if(isUnsigned) {
                    /*
                     * If the tile memory data was read from the region 0x8000 -
                     * 0x8FFF then the tileNumber read from the tileAddress is an
                     * unsigned byte and the identifier ranges from 0 to 255.
                     */
                    tileLocation = tileLocation + (tileNumber * SIZE_OF_TILE_IN_MEMORY)
                }else {
                    /*
                     * If the memory region was 0x8800 - 0x97FF, then the
                     * identifier is signed and ranges from -127 to 127.
                     */
                    final int OFFSET = 128
                    tileLocation = tileLocation + ((tileNumber + OFFSET) * SIZE_OF_TILE_IN_MEMORY)
                }

                /*
                 * This calculation determines the row of pixels of the current
                 * tile that the scanline is on.
                 */
                int tilePixelRow = yPosition % TILE_PIXEL_HEIGHT

                /*
                 * Each row of pixels is made by combining two rows of tile data.
                 */
                tilePixelRow = tilePixelRow * 2
                int pixelData1 = memoryManager.readMemory(tileLocation + tilePixelRow)
                int pixelData2 = memoryManager.readMemory(tileLocation + tilePixelRow + 1)

                int colourBit = xPosition % TILE_PIXEL_WIDTH

                /*
                 * Get the xPosition of the bit with respect to the tile.
                 * However, the bit's index position is not the same as the bit's
                 * significance. So, for the 8 bits making up a tile row, index
                 * position 0 of the bit is really bit position 7. Index position
                 * 1 is bit position 6. And so the correct conversion is made by
                 * first subtracting 7 and take the absolute value.
                 */
                colourBit = Math.abs((colourBit - 7))

                /*
                 * Two rows of bits are combined to form a single row of pixels.
                 * The combination of the rows allows for pixel colours to be
                 * generated. The combined bits result in a value that maps to
                 * one of four possible colours.
                 */
                int colourNumber = BitUtil.getValue(pixelData2, colourBit)
                colourNumber = colourNumber << 1
                colourNumber = colourNumber | BitUtil.getValue(pixelData1, colourBit)

                /*
                 * The BG (Background) palette data register assigns gray shades
                 * to the colour numbers of the BG and window tiles.
                 * 
                 * Bit 7-6 - Shade for Colour Number 3
                 * Bit 5-4 - Shade for Colour Number 2
                 * Bit 3-2 - Shade for Colour Number 1
                 * Bit 1-0 - Shade for Colour Number 0
                 * 
                 * The four possible shades of gray are:
                 * 0 White
                 * 1 Light gray
                 * 2 Dark Gray
                 * 3 Black
                 */
                final int BG_PALETTE_DATA = 0xFF47

                Colour colour = getColour(colourNumber, BG_PALETTE_DATA)
                int red = 0
                int green = 0
                int blue = 0

                switch(colour) {
                    case Colour.WHITE : red = 255; green = 255; blue = 255; break
                    case Colour.LIGHT_GRAY: red = 0xCC; green = 0xCC; blue = 0xCC; break
                    case Colour.DARK_GRAY: red = 0x77; green = 0x77; blue = 0x77; break
                }

                int scanline = memoryManager.readMemory(LCD.LY_REGISTER)

                if(scanline < 0 || scanline > (LCD.HEIGHT - 1) || pixel < 0 || pixel > (LCD.WIDTH - 1)) {
                    /*
                     * Skip the current iteration if scanline or pixel are not
                     * within the screen's bounds.
                     */
                    return /* really just a continue statement */
                }

                screenData[pixel][scanline][0] = red
                screenData[pixel][scanline][1] = green
                screenData[pixel][scanline][2] = blue
            }
        }
    }

    private void renderSprites() {
        /*
         * Sprite attributes are found in the sprite attribute table located at
         * 0xFE00 - 0xFE9F. Each sprite uses 4 bytes of this memory region for
         * its associated attributes. The attributes are as follows:o
         * 
         * 0: Sprite Y Position: Position of the sprite on the Y axis of the
         * viewing display minus 16
         * 
         * 1: Sprite X Position: Position of the sprite on the X axis of the
         * viewing display minus 8
         * 
         * 2: Pattern number: This is the sprite identifier used for looking up
         * the sprite data in memory region 0x8000-0x8FFF
         * 
         * 3: Bits of this byte are the sprite's attributes, explained below.
         * 
         * SPRITE ATTRIBUTES:
         * Bit 7: Sprite to Background Priority
         * If this flag is 0 then the sprite is rendered above the background and
         * window. Otherwise it is hidden behind both. However, if the background
         * and window is white, it is rendered above.
         * 
         * Bit 6: Y-Flip
         * If this bit is set then the sprite is mirrored vertically, this is
         * useful for turning sprites upside down.
         * 
         * Bit 5: X-Flip
         * If this bit is set then the sprite is mirrored horizontally, useful
         * for changing the direction of sprites.
         * 
         * Bit 4: Palette Number
         * Sprites get their monochrome palette from either 0xFF48 or 0xFF49. If
         * this bit is set then the palette is from the former, otherwise, the
         * latter.
         * 
         * The other bits are unused.
         */

        /*
         * There are 40 sprite tiles located in memory region 0x8000 - 0x8FFF.
         */
        final int SPRITE_TILE_AMT = 40

        /*
         * A sprite can be either 8x8 or 8x16 pixels, this is determined by the
         * sprite's attributes.
         */
        boolean use8x16 = false

        def spriteAttributeBit = [
            BACKGROUND_PRIORITY : 7,
            Y_FLIP : 6,
            X_FLIP : 5,
            PALETTE_NUMBER : 4
        ].asUnmodifiable()

        if(BitUtil.isSet(LCD.LCDC_REGISTER, LCD.ControlRegisterBit.OBJ_SPRITE_SIZE)) {
            use8x16 = true
        }

        final int SPRITE_SIZE = 4
        final int SPRITE_ATTRIBUTE_TABLE = 0xFE00

        SPRITE_TILE_AMT.times { sprite->
            /*
             * Sprite takes 4 bytes in the sprite attribute table. So calculate
             * it's base address and read its associated bytes offset from the
             * calculated address.
             */
            int spriteIndex = sprite * SPRITE_SIZE
            int yPosition = memoryManager.readMemory(SPRITE_ATTRIBUTE_TABLE + spriteIndex) - 16
            int xPosition = memoryManager.readMemory(SPRITE_ATTRIBUTE_TABLE + spriteIndex + 1) - 8
            int tileLocation = memoryManager.readMemory(SPRITE_ATTRIBUTE_TABLE + spriteIndex + 2)
            int attributes = memoryManager.readMemory(SPRITE_ATTRIBUTE_TABLE + spriteIndex + 3)

            boolean yFlip = BitUtil.isSet(attributes, spriteAttributeBit.Y_FLIP)
            boolean xFlip = BitUtil.isSet(attributes, spriteAttributeBit.X_FLIP)

            int scanline = memoryManager.readMemory(LCD.LY_REGISTER)

            int ySize = 8

            if(use8x16) {
                ySize = 16
            }

            /*
             * Determine if this sprite intercepts with the scanline
             */
            if((scanline >= yPosition) && (scanline < (yPosition+ySize))) {
                int line = scanline - yPosition

                if(yFlip) {
                    /*
                     * Read the sprite in backwards
                     */
                    line = Math.abs(line - ySize)
                }

                /*
                 * Two rows of bits combine to form a single line of pixels on the
                 * screen. The line represents the row of bits on the tile. For
                 * example, if the current line is 0, then line 0 and 1 are
                 * combined to form the row of pixels. If the current line is 1,
                 * then line's 2 and 3 are combined, etc.
                 */
                line = line * 2

                int tileDataAddress = (TILE_DATA_LOCATION_1 + (tileLocation * SIZE_OF_TILE_IN_MEMORY)) + line
                /* Read the current row of tile bits */
                int tileData1 = memoryManager.readMemory(tileDataAddress)
                /* Read the next row of tile bits */
                int tileData2 = memoryManager.readMemory(tileDataAddress + 1)

                /*
                 * Now combine the two rows of bits to form a single line of
                 * pixels. The tileData is processed from right to left as this
                 * naturally aligns with the pixels. Since pixel 0 is bit 7 of the
                 * colour data, pixel 1 is bit 6, etc.
                 */

                for(int tilePixel = 7; tilePixel >=0; tilePixel--) {
                    int colourBit = tilePixel

                    /*
                     * Read sprite backwards if flipped along the x-axis
                     */
                    if(xFlip) {
                        colourBit = Math.abs(colourBit - 7)
                    }

                    /* The rest of code is roughly the same as what happens for the tiles */
                    int colourNumber = BitUtil.getValue(tileData2, colourBit)
                    colourNumber = colourNumber << 1
                    colourNumber = colourNumber | BitUtil.getValue(tileData1, colourBit)

                    /*
                     * Sprites can use either of these two palettes based on the
                     * bit value for their palette number attribute.
                     */
                    final int SPRITE_PALETTE_0 = 0xFF48
                    final int SPRITE_PALETTE_1 = 0xFF49

                    int colourAddress = 0

                    if(BitUtil.isSet(attributes, spriteAttributeBit.PALETTE_NUMBER)) {
                        colourAddress = SPRITE_PALETTE_1
                    }else {
                        colourAddress = SPRITE_PALETTE_0
                    }

                    Colour colour = getColour(colourNumber, colourAddress)

                    /*
                     * White is transparent for sprites
                     */
                    if(colour == Colour.WHITE) {
                        return /* skip this iteration */
                    }

                    int red = 0
                    int green = 0
                    int blue = 0

                    switch(colour) {
                        case Colour.WHITE : red = 255; green = 255; blue = 255; break
                        case Colour.LIGHT_GRAY: red = 0xCC; green = 0xCC; blue = 0xCC; break
                        case Colour.DARK_GRAY: red = 0x77; green = 0x77; blue = 0x77; break
                    }

                    /*
                     * Convert the tilePixel into a screen pixel
                     */
                    int pixel = 0 - tilePixel
                    pixel = pixel + 7

                    pixel = xPosition + pixel

                    /*
                     * Final sanity check!
                     */
                    if((scanline < 0) || (scanline > (LCD.HEIGHT - 1)) || pixel < 0 || pixel > (LCD.WIDTH - 1)) {
                        continue
                    }

                    screenData[pixel][scanline][0] = red
                    screenData[pixel][scanline][1] = green
                    screenData[pixel][scanline][2] = blue
                }
            }
        }
    }

    private Colour getColour(int colourNumber, int address){
        Colour colour = Colour.WHITE

        int palette = memoryManager.readMemory(address)
        int hi = 0
        int lo = 0

        switch(colourNumber){
            case 0: hi = 1; lo = 0; break
            case 1: hi = 3; lo = 2; break
            case 2: hi = 5; lo = 4; break
            case 3: hi = 7; lo = 6; break
        }

        int c = 0
        c = BitUtil.getValue(palette, hi) << 1
        c = c | BitUtil.getValue(palette, lo)

        switch(c){
            case 0: colour = Colour.WHITE; break
            case 1: colour = Colour.LIGHT_GRAY; break
            case 2: colour = Colour.DARK_GRAY; break
            case 3: colour = Colour.BLACK; break
        }

        return colour
    }
}