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
            cycles = executeOpcode(opcode)
        }catch(Exception e) {
            println e.message
            System.exit(1)
        }
        return cycles
    }
    
    private int executeOpcode(opcode) {
        switch(opcode){
            /* No-op */
            case 0x00:
            return 4
            
            /* 8-Bit Loads */
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
            
            /* Register Loads */
            case 0x7F:
            register.A = cpuRegisterLoad(register.A)
            return 4
            case 0x78:
            register.A = cpuRegisterLoad(register.B)
            return 4
            case 0x79:
            register.A = cpuRegisterLoad(register.C)
            return 4
            case 0x7A:
            register.A = cpuRegisterLoad(register.D)
            return 4
            case 0x7B:
            register.A = cpuRegisterLoad(register.E)
            return 4
            case 0x7C:
            register.A = cpuRegisterLoad(register.H)
            return 4
            case 0x7D:
            register.A = cpuRegisterLoad(register.L)
            return 4
            case 0x40:
            register.B = cpuRegisterLoad(register.B)
            return 4
            case 0x41:
            register.B = cpuRegisterLoad(register.C)
            return 4
            case 0x42:
            register.B = cpuRegisterLoad(register.D)
            return 4
            case 0x43:
            register.B = cpuRegisterLoad(register.E)
            return 4
            case 0x44:
            register.B = cpuRegisterLoad(register.H)
            return 4
            case 0x45:
            register.B = cpuRegisterLoad(register.L)
            return 4
            case 0x48:
            register.C = cpuRegisterLoad(register.B)
            return 4
            case 0x49:
            register.C = cpuRegisterLoad(register.C)
            return 4
            case 0x4A:
            register.C = cpuRegisterLoad(register.D)
            return 4
            case 0x4B:
            register.C = cpuRegisterLoad(register.E)
            return 4
            case 0x4C:
            register.C = cpuRegisterLoad(register.H)
            return 4
            case 0x4D:
            register.C = cpuRegisterLoad(register.L)
            return 4
            case 0x50:
            register.D = cpuRegisterLoad(register.B)
            return 4
            case 0x51:
            register.D = cpuRegisterLoad(register.C)
            return 4
            case 0x52:
            register.D = cpuRegisterLoad(register.D)
            return 4
            case 0x53:
            register.D = cpuRegisterLoad(register.E)
            return 4
            case 0x54:
            register.D = cpuRegisterLoad(register.H)
            return 4
            case 0x55:
            register.D = cpuRegisterLoad(register.L)
            return 4
            case 0x58:
            register.E = cpuRegisterLoad(register.B)
            return 4
            case 0x59:
            register.E = cpuRegisterLoad(register.C)
            return 4
            case 0x5A:
            register.E = cpuRegisterLoad(register.D)
            return 4
            case 0x5B:
            register.E = cpuRegisterLoad(register.E)
            return 4
            case 0x5C:
            register.E = cpuRegisterLoad(register.H)
            return 4
            case 0x5D:
            register.E = cpuRegisterLoad(register.L)
            return 4
            case 0x60:
            register.H = cpuRegisterLoad(register.B)
            return 4
            case 0x61:
            register.H = cpuRegisterLoad(register.C)
            return 4
            case 0x62:
            register.H = cpuRegisterLoad(register.D)
            return 4
            case 0x63:
            register.H = cpuRegisterLoad(register.E)
            return 4
            case 0x64:
            register.H = cpuRegisterLoad(register.H)
            return 4
            case 0x65:
            register.H = cpuRegisterLoad(register.L)
            return 4
            case 0x68:
            register.L = cpuRegisterLoad(register.B)
            return 4
            case 0x69:
            register.L = cpuRegisterLoad(register.C)
            return 4
            case 0x6A:
            register.L = cpuRegisterLoad(register.D)
            return 4
            case 0x6B:
            register.L = cpuRegisterLoad(register.E)
            return 4
            case 0x6C:
            register.L = cpuRegisterLoad(register.H)
            return 4
            case 0x6D:
            register.L = cpuRegisterLoad(register.L)
            return 4
            
            case 0x80:
            register.A = cpu8BitAdd(register.A, register.B, false, false) 
            return 4
            
            case 0x90:
            register.A = cpu8BitSub(register.A, register.B, false, false)
            return 4
            
            case 0xAF:
            register.A = cpu8BitXOR(register.A, register.A, false)
            return 4
            
            case 0x20:
            cpuJumpImmediate(true, register.FLAG_Z, false)
            return 8
            
            case 0xCC:
            cpuCall(true, register.FLAG_Z, true)
            return 12

            case 0xD0:
            cpuReturn(true, register.FLAG_C, false)
            return 8
            
            case 0xCB:
            try {
                return executeExtendedOpcode()
            }catch(Exception e) {
                throw e
            }

            default:
                def hexCode = java.lang.String.format("0x%2X", opcode)
                throw new Exception("Unrecognized opcode: " + hexCode)
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
           def hexCode = java.lang.String.format("0x%2X", opcode)
           throw new Exception("Unrecognized extended opcode: " + hexCode) 
        }
    }
    
    private int cpu8BitLoad() {
        int n = memoryManager.readMemory(register.pc)
        register.pc++
        return n
    }
    
    private int cpuRegisterLoad(int register2) {
       return register2 
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
       register.clearAllFlags()
       
       if(reg == 0) {
           register.setZ()
       }
       
       int halfCarry = initialValue & 0xF
       halfCarry = halfCarry + (runningSum & 0xF)
       
       if(halfCarry > 0xF) {
           register.setH()
       }
       
       if((initialValue + runningSum) > 0xFF) {
           register.setC()
       }

       return reg
    } 
    
    private int cpu8BitSub(int reg, int value, boolean useImmediate, boolean subCarry) {
       int initialValue = reg
       int runningDifference = 0 
       
       if(useImmediate) {
           int n = memoryManager.readMemory(register.pc)
           register.pc++
           runningDifference = n
       }else {
           runningDifference = value
       }
       
       if(subCarry) {
           if(register.isC()) {
               runningDifference++
           }
       }
       
       reg = reg - runningDifference
       
       /* now set flags */
       register.clearAllFlags()
       
       if(reg == 0) {
          register.setZ() 
       }
       
       register.setN()
       
       if(initialValue < runningDifference) {
           register.setC()
       }
       
       int halfCarry = initialValue & 0xF
       halfCarry = halfCarry - (runningDifference & 0xF)
       
       if(halfCarry < 0) {
           register.setH()
       }
       
       return reg
    }
    
    private int cpu8BitXOR(int reg, int value, boolean useImmediate) {

       int xor = 0
       
       if(useImmediate) {
          int n = memoryManager.readMemory(register.pc) 
          register.pc++
          xor = n
       }else {
           xor = value
       }
       
       reg = reg ^ xor
       
       register.clearAllFlags()
       
       if(reg == 0) {
           register.setZ()
       }

       return reg
    }
    
    public void cpuJumpImmediate(boolean useCondition, int flag, boolean condition) {
       int n = memoryManager.readMemory(register.pc) 
       
       if(!useCondition) {
           /*
            * Jump unconditionally
            */
           register.pc = register.pc + n
       }else if(register.isSet(flag) == condition) {
           /*
            * Only jump if the condition is met
            */
           register.pc = register.pc + n
       }
       register.pc++
    }
    
    private void cpuCall(boolean useCondition, int flag, boolean condition) {
       int word = memoryManager.readWord() 
       /*
        * Advance 2 positions ahead because two bytes were just read.
        */
       register.pc = register.pc + 2
       
       if(!useCondition) {
           memoryManager.push(register.pc)
           register.pc = word
           return
       }
       
       if(register.isSet(flag) == condition) {
          memoryManager.push(register.pc) 
          register.pc = word
       }
    }
    
    private void cpuReturn(boolean useCondition, int flag, boolean condition) {
       if(!useCondition) {
           register.pc = memoryManager.pop()
           return
       }
       
       if(register.isSet(flag) == condition) {
           register.pc = memoryManager.pop()
       } 
    }
    
    private int cpuRRC(int reg) {
       boolean isLSBSet = BitUtil.isSet(reg, 0)  
       
       register.clearAllFlags()
       
       reg = reg >> 1
       
       if(isLSBSet) {
           register.setC()
           reg = BitUtil.setBit(reg, 7)
       }
       
       if(reg == 0) {
           register.setZ()
       }
       
       return reg
    }
    
    private void cpuTestBit(int reg, int bit) {
       /*
        * This tests the bit of a byte and sets the following flags: 
        * 
        * FLAG_Z : set to 1 if the bit is 0
        * FLAG_N : Set to 0
        * FLAG_C : Unchanged
        * FLAG_H : Set to 1
        */
       
       if(BitUtil.isSet(reg, bit)) {
           register.clearZ()
       }else {
           register.setZ()
       }
       
       register.clearN()
       register.setH()
    }
}
