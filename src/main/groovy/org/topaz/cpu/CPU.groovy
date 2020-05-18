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
