package org.topaz.cpu

import org.topaz.cpu.Register
import org.topaz.InterruptHandler
import org.topaz.MemoryManager

class CPU{
    Register register
    MemoryManager memoryManager
    
    int executeNextOpcode() {
        int cycles = 0
        int opcode = memoryManager.readMemory(register.pc)
        register.pc++

        try {
            cycles = executeOpcode()
        }catch(Exception e) {
            println e.message
            System.exit(1)
        }
        return cycles
    }
    
    private int executeOpcode(opcode) {
        switch(opcode){
            case 0x06:
            register.B = cpu8BitLoad()
            return 8
            
            case 0x0E:
            register.C = cpu8BitLoad()
            return 8
            
            case 0x16:
            register.D = cpu8BitLoad()
            return 8
            
            case 0x1E:
            register.E = cpu8BitLoad()
            return 8
            
            case 0x26:
            register.H = cpu8BitLoad()
            return 8
            
            case 0x2E:
            register.L = cpu8BitLoad()
            return 8

            default:
                throw new Exception("Unrecognized opcode: " + opcode)
        }
    }
    
    private int cpu8BitLoad() {
        int n = memoryManager.readMemory(register.pc)
        register.pc++
        return n
    }
}