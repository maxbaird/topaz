package org.topaz

import org.topaz.MemoryManager
import org.topaz.util.BitUtil

class Timer{
    /*
     * The Gameboy has a timer in memory which counts up at a certain frequency
     * and requests an interrupt and resets when it overflows i.e., reaching a
     * value larger than 255. The timer counter is at address 0xFF05 and
     * incremented by the frequency stored at address 0xFF07. The value 
     * the timer is reset to is held at address 0xFF06.
     */

    final int TIMA = 0xFF05 /* Timer Counter */
    final int TMA = 0xFF06  /* Timer Modulator */
    /* Timer Controller
     * Bit 2    - Timer Stop  (0=Stop, 1=Start)
     * Bits 1-0 - Input Clock Select
     *            00:   4096 Hz    (~4194 Hz SGB)
     *            01: 262144 Hz  (~268400 Hz SGB)
     *            10:  65536 Hz   (~67110 Hz SGB)
     *            11:  16384 Hz   (~16780 Hz SGB)
     */
    final int TMC = 0xFF07  /* The Timer Controller */

    /*
     * The CPU runs at a clock speed of 4194304Hz, therefore, if we
     * know the timer frequency the number of clock cycles that must
     * pass between incrementing the register can be calculated.
     */
    final int CLOCKSPEED = 4194304
    int timerCounter = 1024
    int dividerCounter = 0

    MemoryManager memoryManager

    public void updateTimer(int cycles) {
        incrementDividerRegister(cycles)

        if(isClockEnabled()) {
            timerCounter = timerCounter - cycles

            if(timerCounter <= 0) {
                /*
                 * If timerCounter <= 0 enough CPU cycles have passed and the
                 * timer should be updated.
                 */
                setClockFrequency()

                if(memoryManager.readMemory(TIMA) == 255) {
                    /*
                     * If the timer overflows, reset it to the value at address 0xFF06 (TMA)
                     * and request an interrupt.
                     */
                    memoryManager.writeMemory(TIMA, memoryManager.readMemory(TMA))
                    requestInterrupt(2)
                }else {
                    /*
                     * Otherwise, simply increment the timer
                     */
                    memoryManager.writeMemory(TIMA, memoryManager.readMemory(TIMA) + 1)
                }
            }
        }
    }

    private boolean isClockEnabled() {
        /*
         * This function checks the timer controller (TMC at address 0xFF07).
         * Bit 2 of this address either starts or stops the timer respectively
         * if it is set or not.
         */
        return BitUtil.testBit(memoryManager.readMemory(TMC), 2)
    }
    
    private void setClockFrequency() {
        int frequency = getClockFrequency()
        
        switch(frequency) {
           case 0: timerCounter = 1024; break /* frequency is 4096 */
           case 1: timerCounter = 16; break /* frequency is 262144 */
           case 2: timerCounter = 64; break /* frequency is 65536 */
           case 3: timerCounter = 256; break /* frequency is 16832 */
        }
    }
    
    private void incrementDividerRegister(int cycles) {
        /*
         * The timer divider register is located at address 0xFF04 and
         * continuously increments to 255 and then reset to 0. No interrupts are
         * cased. It counts at a frequency of 16382 and so needs updating every
         * 256 CPU cycles.
         */
        dividerCounter = dividerCounter + cycles
        
        if(dividerCounter >= 256) {
            dividerCounter = 0
            /*
             * The divider register is incremented directly instead of using the
             * writeMemory method in the MemoryManager. This is because the
             * hardware does not allow direct writing to the divide register.
             * Whenever direct writing is attempted, the register is reset to 0
             * and the writeMemory method follows this behaviour.
             */
            memoryManager.rom[0xFF04]++
        }
    }
    
    public int getClockFrequency() {
       /*
        * The clock frequency is combination of bits 1 and 0 of the Timer
        * controller (TMC at address 0xFF07).
        */
        return memoryManager.readMemory(TMC) & 0x3
    }
}