package org.topaz.lcd 
import org.topaz.MemoryManager
import org.topaz.InterruptHandler

class LCD{
    /*
     * The current scanline to be drawn to screen is stored at address 0xFF44
     */
    static final int CURRENT_SCANLINE = 0xFF44
    
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
          /* Move to next scanline. Memory is accessed directly instead of
           * using the writeMemory method of the memory manager. This is because
           * any writes to 0xFF44 (CURRENT_SCANLINE) must reset the current
           * scanline to 0. Which is what the memory manager is programmed to
           * do.
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
        
    }
    
    private boolean isLCDEnabled() {
        return true
    }
    
    private void drawScanLine() {
        
    }
}