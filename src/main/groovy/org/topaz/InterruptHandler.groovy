package org.topaz

import org.topaz.cpu.Register
import org.topaz.util.BitUtil
import org.topaz.cpu.CPU

class InterruptHandler{
    static final int V_BLANK_INTERRUPT = 0
    static final int LCD_INTERRUPT = 1
    static final int TIMER_INTERRUPT = 2
    static final int JOYPAD_INTERRUPT = 4

    /*
     * Map for the Interrupt Service Routines for each interrupt.
     */
    private static final def ISR = [V_BLANK:0x40, LCD:0x48, TIMER:0x50, JOYPAD:0x60]

    /*
     * The Interrupt Enable register (IE_REGISTER) is located at 0xFFFF and is
     * used to enable and disable specific interrupts. For example, if the timer
     * interrupt is requested, it will only be serviced if its corresponding bit
     * is enabled in the IE_REGISTER.
     */
    private final int IE_REGISTER = 0xFFFF

    /*
     * The Interrupt Request register (IF_REGISTER) is at memory address 0xFF0F
     * and is used to request interrupts by setting its corresponding bit in the
     * IF_REGISTER.
     */
    private final int IF_REGISTER = 0xFF0F

    /*
     * The interruptsEnabled part of game memory. It is just a value that is
     * toggled to enable or disable the servicing of interrupts. A false value
     * means interrupts are disabled.
     */
    boolean interruptsEnabled = false

    MemoryManager memoryManager
    CPU cpu /* to access stack pointer */


    public void requestInterrupt(int interruptId) {
        /*
         * This function sets the bit corresponding to the interrupt's ID in the
         * interrupt request (IF) register.
         */
        int request = memoryManager.readMemory(IF_REGISTER)
        request = BitUtil.setBit(request, interruptId)
        memoryManager.writeMemory(IF_REGISTER, request)
    }

    public void handleInterrupts() {
        /*
         * THis method is used in the main emulation loop to handle
         * any interrupts.
         */
        if(interruptsEnabled) {/* Ensure interrupts are enabled */

            /*
             * Get all interrupts requested
             */
            int request = memoryManager.readMemory(IF_REGISTER)

            /*
             * Get all interrupts enabled
             */
            int enabled = memoryManager.readMemory(IE_REGISTER)

            if(request > 0) {
                /*
                 * If any interrupts were requested, they are serviced in order
                 * of their priority. Only enabled interrupts are serviced.
                 */
                5.times {it ->
                    if(BitUtil.isSet(request, it)) {
                        if(BitUtil.isSet(enabled, it)) {
                            serviceInterrupt(it)
                        }
                    }
                }
            }
        }
    }

    void serviceInterrupt(int interruptId) {
        interruptsEnabled = false
        int request = memoryManager.readMemory(IF_REGISTER)
        request = BitUtil.resetBit(request, interruptId)
        memoryManager.writeMemory(IF_REGISTER, request)
        
        cpu.register.push(cpu.register.pc) 
        
        switch(interruptId) {
           case V_BLANK_INTERRUPT : cpu.register.pc = ISR.V_BLANK; break
           case LCD_INTERRUPT : cpu.register.pc = ISR.LCD; break
           case TIMER_INTERRUPT : cpu.register.pc = ISR.TIMER; break
           case JOYPAD_INTERRUPT : cpu.register.pc = ISR.JOYPAD; break
        }
    }
}