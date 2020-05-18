package org.topaz.cpu

import org.topaz.cpu.Register
import org.topaz.InterruptHandler
import org.topaz.MemoryManager
import org.topaz.util.BitUtil

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
            
            case 0x80:
            register.A = cpu8BitAdd(register.A, register.B, false, false) 
            return 4
            
            case 0xCB:
            try {
                return executeExtendedOpcode()
            }catch(Exception e) {
                throw e
            }

            default:
                throw new Exception("Unrecognized opcode: " + opcode)
        }
    }
    
    private int executeExtendedOpcode() {
        /*
         * When the opcode 0xCB is encountered the next immediate byte needs to
         * be decoded and treated as an opcode.
         */
        int opcode = memoryManager.readMemory(register.pc)
        register.pc++
        
        switch(opcode) {
           default:
           throw new Exception("Unrecognized extended opcode: " + opcode) 
        }
    }
    
    private int cpu8BitLoad() {
        int n = memoryManager.readMemory(register.pc)
        register.pc++
        return n
    }
    
    private int cpu8BitAdd(int reg, int value, boolean addImmediate, boolean addCarry) {
       int initialValue = register
       int runningSum = 0
       
       if(addImmediate) {
          int n = memoryManager.readMemory(register.pc)
          register.pc++
          runningSum = n
       } else {
           runningSum = value
       }
       
       if(addCarry) {
          if(register.isC()) {
              runningSum++
          }
       }
       
       reg = reg + runningSum
       
       /* Set flags */
       register.F = 0
       
       if(reg == 0) {
           register.setZ(true)
       }
       
       int halfCarry = initialValue & 0xF
       halfCarry = halfCarry + (runningSum & 0xF)
       
       if(halfCarry > 0xF) {
           register.setH(true)
       }
       
       if((initialValue + runningSum) > 0xFF) {
           register.setC(true)
       }

       return reg
    } 
}