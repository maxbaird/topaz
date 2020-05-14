package org.topaz.gpu

import org.topaz.gpu.LCD
import org.topaz.MemoryManager
import org.topaz.InterruptHandler
import org.topaz.util.BitUtil

class GPU{
    static final int CYCLES_BETWEEN_SCANLINES = 456

    MemoryManager memoryManager
    InterruptHandler interruptHandler

    /*
     * It takes 456 CPU cycles to draw each scanline. In the main emulator loop
     * the graphics is updated after the execution of each opcode and the total
     * number of CPU cycles executed thus far is subtracted from
     * scanLineCounter. If the result is 0 or less, it is time to draw the next
     * scanline.
     */
    static int SCAN_LINE_CYCLES_COUNTER = CYCLES_BETWEEN_SCANLINES
    
    /*
     * The screen resolution is 160x144, however, the Gameboy actually draws 153
     * scanlines instead of 144. The extra 8 scanlines are invisible and
     * constitute the vertical blank period (i.e., between the 144th and 153rd
     * scanline).
     */
    static final int V_BLANK_SCANLINE_START = 144
    static final int V_BLANK_SCANLINE_END = 153

    private LCD lcd
    
    public GPU(MemoryManager memoryManager, InterruptHandler interruptHandler) {
        this.memoryManager = memoryManager
        this.interruptHandler = interruptHandler
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
//     |                |                          WIN_Y        |
//     |                |                            |          |
//     |<---SCROLL_X--->|                            V          |
//     |                |                   +---------------+   |
//     |                |                   |               |   |
//     |                | <-----WIN_X------>|     window    |   |
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
               tileData = 0x8000
           }else {
               tileData = 0x8800
               
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
           
           final int NUMBER_OF_HORIZONTAL_PIXELS = 160
           
           NUMBER_OF_HORIZONTAL_PIXELS.times {pixel->
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
               
               final int TILE_PIXEL_WIDTH = 8
               /*
                * This calculation determines which of the 32 horizontal tiles
                * on the 256x256 grid of tiles the current pixel's xPosition
                * falls within, i.e., which tile column.
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
               
               final int SIZE_OF_TILE_IN_MEMORY = 16
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
           }
       }
   } 
   
   private void renderSprites() {
       
   }
}