package org.topaz.lcd 
import org.topaz.MemoryManager
import org.topaz.InterruptHandler

class LCD{
    static final int SCANLINE_ADDR = 0xFF44
    final int CYCLES_BETWEEN_SCANLINES = 456
    final int V_BLANK_START = 144
    final int V_BLANK_END = 153
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
            /* Move to next scanline */
            memoryManager.rom[SCANLINE_ADDR]++
            int currentLine = memoryManager.readMemory(SCANLINE_ADDR)
            scanLineCounter = CYCLES_BETWEEN_SCANLINES
            
            if(currentLine == V_BLANK_START) {
               interruptHandler.requestInterrupt(InterruptHandler.V_BLANK_INTERRUPT) 
            }else if(currentLine > V_BLANK_END) {
                memoryManager.rom[SCANLINE_ADDR] = 0
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