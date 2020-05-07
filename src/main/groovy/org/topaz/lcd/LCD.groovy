package org.topaz.lcd 
import org.topaz.MemoryManager
import org.topaz.InterruptHandler
import org.topaz.util.BitUtil

class LCD{
    /*
     * The current scanline to be drawn to screen is stored at address 0xFF44
     */
    static final int CURRENT_SCANLINE = 0xFF44
    
    /* 
     * The current LCD status is held at address 0xFF41.The LCD goes through 4
     * modes when drawing scanlines, Bits 1 and 0 represent the current LCD mode
     * as follows:
     * 00: H-Blank
     * 01: V-Blank
     * 10: Searching for sprite attributes
     * 11: Transfer data to LCD Driver 
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
    
    final int CYCLES_BETWEEN_SCANLINES = 456
    
    /*
     * The screen resolution is 160x144, however, the Gameboy actually draws 153
     * scanlines instead of 144. The extra 8 scanlines are invisible and
     * constitute the vertical blank period (i.e., between the 144th and 153rd
     * scanline).
     */
    final int V_BLANK_START = 144
    final int V_BLANK_END = 153
    /*
     * It takes 456 CPU cycles to draw each scanline. In the main emulator loop
     * the graphics is updated after the execution of each opcode and the total
     * number of CPU cycles executed thus far is subtracted from
     * scanLineCounter. If the result is 0 or less, it is time to draw the next
     * scanline.
     */
    int scanLineCounter = CYCLES_BETWEEN_SCANLINES
    
    MemoryManager memoryManager
    InterruptHandler interruptHandler
    
    void updateGraphics(int cycles) {
        setLCDStatus()
        
        if(isLCDEnabled()) {
            scanLineCounter = scanLineCounter - cycles
        }else {
            return
        }
        
        if(scanLineCounter <= 0) {
          /* 
           * Move to next scanline. Memory is accessed directly instead of using
           * the writeMemory method of the memory manager. This is because any
           * writes to 0xFF44 (CURRENT_SCANLINE) must reset the current scanline
           * to 0. Which is what the memory manager is programmed to do.
           */
            memoryManager.rom[CURRENT_SCANLINE]++
            int currentLine = memoryManager.readMemory(CURRENT_SCANLINE)
            
            /* Reset the scanline counter */
            scanLineCounter = CYCLES_BETWEEN_SCANLINES
            
            if(currentLine == V_BLANK_START) {
              /* Request the appropriate interrupt if in a vertical blank period */
                interruptHandler.requestInterrupt(InterruptHandler.V_BLANK_INTERRUPT) 
            }else if(currentLine > V_BLANK_END) {
                /*
                 * If we are beyond the vertical blank period, the current
                 * scanline must then be reset.
                 */
                memoryManager.rom[CURRENT_SCANLINE] = 0
            }else if(currentLine < V_BLANK_START) {
               drawScanLine() 
            }
        }
    }
    
    private void setLCDStatus() {
        int status = memoryManager.readMemory(LCD_STATUS)
        
        if(!isLCDEnabled()) {
            /*
             * When the LCD is disabled reset scanLineCounter and set the
             * current scanline to 0.
             */
            scanLineCounter = CYCLES_BETWEEN_SCANLINES
            memoryManager.rom[CURRENT_SCANLINE] = 0
            
            /*
             * Also set the LCD mode to 1
             */
            status &= 252 /* Set bits 1 and 2 to 0 */
            status = BitUtil.setBit(status, 0) /* Set bit 0 to 1 */
            memoryManager.writeMemory(LCD_STATUS, status)
            return
        }
        
    }
    
    private boolean isLCDEnabled() {
        return true
    }
    
    private void drawScanLine() {
        
    }
}