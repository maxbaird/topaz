package org.topaz

import org.topaz.util.BitUtil
import org.topaz.cpu.CPU2

class InterruptHandler{
    public static final int V_BLANK_INTERRUPT = 0
    static final int LCD_INTERRUPT = 1
    static final int TIMER_INTERRUPT = 2
    static final int SERIAL_INTERRUPT = 3
    static final int JOYPAD_INTERRUPT = 4

    /*
     * Map for the Interrupt Service Routines for each interrupt.
     */
    private static final def ISR = [
        V_BLANK : 0x40,
        LCD : 0x48,
        TIMER : 0x50,
        SERIAL : 0x58,
        JOYPAD : 0x60
    ].asUnmodifiable()

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

    MemoryManager memoryManager
    CPU2 cpu /* to access stack pointer */


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
         * Get all interrupts requested
         * 
         * FF0F - IF - Interrupt Flag (R/W)
         *     Bit 0: V-Blank  Interrupt Request (INT 40h)  (1=Request)
         *     Bit 1: LCD STAT Interrupt Request (INT 48h)  (1=Request)
         *     Bit 2: Timer    Interrupt Request (INT 50h)  (1=Request)
         *     Bit 3: Serial   Interrupt Request (INT 58h)  (1=Request)
         *     Bit 4: Joypad   Interrupt Request (INT 60h)  (1=Request)
         */
        int request = memoryManager.readMemory(IF_REGISTER)

        /*
         * Get all interrupts enabled
         * 
         * FFFF - IE - Interrupt Enable (R/W)
         *     Bit 0: V-Blank  Interrupt Enable  (INT 40h)  (1=Enable)
         *     Bit 1: LCD STAT Interrupt Enable  (INT 48h)  (1=Enable)
         *     Bit 2: Timer    Interrupt Enable  (INT 50h)  (1=Enable)
         *     Bit 3: Serial   Interrupt Enable  (INT 58h)  (1=Enable)
         *     Bit 4: Joypad   Interrupt Enable  (INT 60h)  (1=Enable)
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

    private void serviceInterrupt(int interruptId) {
        if(!cpu.interruptMaster && cpu.isHalted) {
            cpu.isHalted = false
            return
        }

        /*
         * The protocol for servicing an interrupt is to set the interrupt
         * enabled switch to false and unset its corresponding bit in the
         * IF_REGISTER.
         */
        cpu.interruptMaster = false
        cpu.isHalted = false
        int request = memoryManager.readMemory(IF_REGISTER)
        request = BitUtil.clearBit(request, interruptId)
        memoryManager.writeMemory(IF_REGISTER, request)

        /*
         * The current execution address is pushed onto the stack
         */
        memoryManager.push(cpu.register.pc.getValue())

        switch(interruptId) {
            /*
             * Based on the interruptId, the appropriate instruction service
             * routine is assigned to the program counter.
             */
            case V_BLANK_INTERRUPT : cpu.register.pc.setValue(ISR.V_BLANK); break
            case LCD_INTERRUPT : cpu.register.pc.setValue(ISR.LCD); break
            case TIMER_INTERRUPT : cpu.register.pc.setValue(ISR.TIMER); break
            case SERIAL_INTERRUPT : cpu.register.pc.setValue(ISR.SERIAL); break
            case JOYPAD_INTERRUPT : cpu.register.pc.setValue(ISR.JOYPAD); break
            default:
                throw new Exception("Unknown Interrupt: " + interruptId)
                System.exit(-1)
        }
    }
}