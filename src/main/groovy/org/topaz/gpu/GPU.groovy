package org.topaz.gpu

import org.topaz.gpu.LCD
import org.topaz.MemoryManager
import org.topaz.InterruptHandler

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
        lcd = new LCD(memoryManager: this.memoryManager, interruptHandler:this.interruptHandler)
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

    public void drawScanLine() {

    }
}