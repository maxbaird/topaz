package org.topaz.gpu 

import org.topaz.MemoryManager
import org.topaz.InterruptHandler
import org.topaz.util.BitUtil
import org.topaz.gpu.GPU

class LCD{
    /*
     * This is the LCDC (LCD Control) register. Bit 7 of this register is
     * checked during the V-Blank to enable or disable the display.
     */
    static final int LCDC_REGISTER = 0xFF40
    
    /*
     * This is a mapping for each bit in the control register. Mostly used by
     * the GPU when rendering tiles and sprites. The main purpose of this map is
     * to ease code readability.
     * 
     * Bit 7 - LCD Display Enable             (0=Off, 1=On)
     * Bit 6 - Window Tile Map Display Select (0=9800-9BFF, 1=9C00-9FFF)//Tile indexes for Tile Maps 1 and 2
     * Bit 5 - Window Display Enable          (0=Off, 1=On)
     * Bit 4 - BG & Window Tile Data Select   (0=8800-97FF, 1=8000-8FFF)//All tiles
     * Bit 3 - BG Tile Map Display Select     (0=9800-9BFF, 1=9C00-9FFF)//Tile Indexes for Tile maps 1 and 2
     * Bit 2 - OBJ (Sprite) Size              (0=8x8, 1=8x16)
     * Bit 1 - OBJ (Sprite) Display Enable    (0=Off, 1=On)
     * Bit 0 - BG Display (for CGB see below) (0=Off, 1=On)
     */
    public static def ControlRegisterBit = [
        DISPLAY_ENABLE : 7,
        WINDOW_TILE_MAP_DISPLAY_SELECT : 6,
        WINDOW_DISPLAY_ENABLE : 5,
        BG_AND_WINDOW_TILE_DATA_SELECT : 4,
        BG_TILE_MAP_DISPLAY_SELECT : 3,
        OBJ_SPRITE_SIZE : 2,
        OBJ_SPRITE_DISPLAY_ENABLE : 1,
        BG_DISPLAY : 0
    ].asUnmodifiable()

    /*
     * The current scanline to be drawn to screen is stored at address 0xFF44.
     * This is known as the LY (LCD Y-coordinate) register and it indicates the
     * vertical line to which the present data is transferred to the LCD Driver.
     * It can take any value between 0 to 153.
     */
    static final int LY_REGISTER = 0xFF44
    
    /*
     * This is the LYC (LY Compare) register. The gameboy compares the value of the
     * LY and LYC registers and when both values are identical, the coincidence bit
     * in the STAT register (LCDC Status Interrupt) becomes set, and (if enabled) an
     * interrupt is requested.
     */
    static final int LYC_REGISTER = 0xFF45
    
    static final int HEIGHT = 144
    static final int WIDTH = 160
    
    /* 
     * The current LCD status is held at address 0xFF41.The LCD goes through 4
     * modes when drawing scanlines, Bits 1 and 0 represent the current LCD mode
     * as follows:
     * 00: During H-Blank
     * 01: During V-Blank
     * 10: During Searching for sprite attributes (OAM-RAM)
     * 11: During Transfer data to LCD Driver 
     * 
     * When the LCD status changes mode between 0b00 to 0b11 (i.e, 0 to 3), an
     * LCD interrupt request can happen. Bits 3,4 and 5 of this register are
     * interrupt enable flags and are set by the game and not the emulator. They
     * represent the following:
     * 
     * Bit 3: Mode 00 Interrupt Enabled
     * Bit 4: Mode 01 Interrupt Enabled
     * Bit 5: Mode 10 Interrupt Enabled
     * 
     * Finally, bit 2 of the status register is the coincidence flag and is set
     * to 1 if the value at address 0xFF44 (the current scanline) is the same as
     * the value at address 0xFF45, otherwise it is set to 0. Bit 6 enables
     * interrupts similar to bits 3 to 5 but has nothing to do with the LCD
     * mode, it is the coincidence interrupt. It is therefore only concerned
     * with bit 2 (the coincidence flag), if bit 2 is set, and the coincidence
     * interrupt (bit 6) is also set, then an LCD interrupt is requested.
     * 
     * The reason the game would be interested in the current scanline is to
     * perform special effects. To perform these effects an interrupt is
     * triggered.
     */
    final int LCD_STATUS = 0xFF41
    
    def LCD_MODE  = [H_BLANK:0, V_BLANK:1, OAM_SPRITE_ATTRIBUTE_SEARCH:2, LCD_DRIVER_TRANSFER:3]
    
    final int COINCIDENCE_INTERRUPT_BIT = 6
    final int OAM_INTERRUPT_BIT = 5 /* Object Attribute Memory (OAM) */
    final int V_BLANK_INTERRUPT_BIT = 4
    final int H_BLANK_INTERRUPT_BIT = 3
    final int COINCIDENCE_FLAG_BIT = 2
    
    MemoryManager memoryManager
    InterruptHandler interruptHandler
    
    public void setLCDStatus() {
        int status = memoryManager.readMemory(LCD_STATUS)
        
        if(!isLCDEnabled()) {
            /*
             * When the LCD is disabled reset scanLineCounter and set the
             * current scanline to 0.
             */
            GPU.SCAN_LINE_CYCLES_COUNTER = GPU.CYCLES_BETWEEN_SCANLINES
            memoryManager.rom[LY_REGISTER] = 0
            
            /*
             * Also set the LCD mode to 1
             */
            status = (status & 252) & 0xFF /* Set bits 1 and 2 to 0 */
            status = BitUtil.setBit(status, 0) /* Set bit 0 to 1 */
            status = BitUtil.clearBit(status, 1)
            memoryManager.writeMemory(LCD_STATUS, status)
            return
        }
        
        int currentScanLine = memoryManager.readMemory(LY_REGISTER)
        int currentMode = status & 0x3

        int mode = LCD_MODE.H_BLANK
        boolean requestInterrupt = false
        
        if(currentScanLine >= GPU.V_BLANK_SCANLINE_START) {
            /*
             * We are currently in the vertical blank (V-Blank) period.
             * So bits 0 and 1 are set to:
             * 01 - for V-Blank
             */
            mode = LCD_MODE.V_BLANK
            status = BitUtil.setBit(status, 0)
            status = BitUtil.clearBit(status, 1)
            requestInterrupt = BitUtil.isSet(status, V_BLANK_INTERRUPT_BIT)
        }else {
            /*
             * It takes 456 clock cycles to draw each scanline. The clock cycles
             * are divided among the active modes for drawing i.e., Mode 0, 2 and 3:
             * 
             * Mode 2: (Searching for sprite attributes) takes the first 80 cycles
             * Mode 3: (transfer to LCD driver) takes 172 clock cycles
             * Mode 0: (H-Blank) takes the remaining cycles
             */
            
            int mode2Cycles = GPU.CYCLES_BETWEEN_SCANLINES - 80
            int mode3Cycles = mode2Cycles - 172
            
            if(GPU.SCAN_LINE_CYCLES_COUNTER >= mode2Cycles) { /* Mode 2 */
               mode = LCD_MODE.OAM_SPRITE_ATTRIBUTE_SEARCH 
               
               /*
                * Currently in Mode 2 (Sprite search) and so bits
                * 0 and 1 are set to:
                * 10 - for sprite attributes
                */
               status = BitUtil.setBit(status, 1)
               status = BitUtil.clearBit(status, 0)
               requestInterrupt = BitUtil.isSet(status, OAM_INTERRUPT_BIT)

            }else if(GPU.SCAN_LINE_CYCLES_COUNTER >= mode3Cycles) { /* Mode 3 */
               mode = LCD_MODE.LCD_DRIVER_TRANSFER

               /*
                * Currently in Mode 3 and so bits
                * 0 and 1 are set to:
                * 11 - for the transfer of data to LCD driver
                */
               status = BitUtil.setBit(status, 1)
               status = BitUtil.setBit(status, 0)
            }else { /* Mode 0 */
               mode = LCD_MODE.H_BLANK 
               
               /*
                * Currently in Mode 0 and bits 0 and 1 are set to:
                * 00 - for H-Blank
                */
               status = BitUtil.clearBit(status, 1)
               status = BitUtil.clearBit(status, 0)
               requestInterrupt = BitUtil.isSet(status, H_BLANK_INTERRUPT_BIT)
            }
            
            /*
             * The current mode of the LCD (currentMode) may not match the mode 
             * that is set based on the CPU cycles. If this is the case, then a
             * mode change has occurred and an interrupt has likely been set.
             * The interrupt must therefore be serviced.
             */
            
            if(requestInterrupt && (mode != currentMode)) {
                interruptHandler.requestInterrupt(InterruptHandler.LCD_INTERRUPT)
            }
            
            //int LY = memoryManager.readMemory(LY_REGISTER)
            int LYC = memoryManager.readMemory(LYC_REGISTER)

            if(currentScanLine == LYC) {
                /*
                 * If the values in the LY and LYC register are equal, then bit
                 * 2 (COINCIDENCE_FLAG_BIT) of the LCD status register is set to
                 * 1.
                 */
                status = BitUtil.setBit(status, COINCIDENCE_FLAG_BIT) 
               
                if(BitUtil.isSet(status, COINCIDENCE_INTERRUPT_BIT)) {
                    /*
                     * If the COINCIDENCE_INTERRUPT_BIT (bit 6) flag is enabled
                     * then an LCD interrupt is requested.
                     */
                    interruptHandler.requestInterrupt(InterruptHandler.LCD_INTERRUPT) 
                }
            }else {
                /*
                 * If the LY and LYC registers do not match, the
                 * COINCIDENCE_FLAG_BIT must be set to 0.
                 */
                status = BitUtil.clearBit(status, COINCIDENCE_FLAG_BIT)
            }
            memoryManager.writeMemory(LCD_STATUS, status)
        }
    }
    
    public boolean isLCDEnabled() {
        /*
         * Bit 7 of the LCDC_REGISTER is responsible enabling or disabling the
         * LCD.
         */
        return BitUtil.isSet(memoryManager.readMemory(LCDC_REGISTER), ControlRegisterBit.DISPLAY_ENABLE) 
    }
}