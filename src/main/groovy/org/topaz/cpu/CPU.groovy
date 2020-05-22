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

    private int executeOpcode(int opcode) {
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

            /* ROM Loads */
            case 0x7E:
                register.A = cpuROMLoad(register.HL)
                return 8
            case 0x46:
                register.B = cpuROMLoad(register.HL)
                return 8
            case 0x4E:
                register.C = cpuROMLoad(register.HL)
                return 8
            case 0x56:
                register.D = cpuROMLoad(register.HL)
                return 8
            case 0x5E:
                register.E = cpuROMLoad(register.HL)
                return 8
            case 0x66:
                register.H = cpuROMLoad(register.HL)
                return 8
            case 0x6E:
                register.L = cpuROMLoad(register.HL)
                return 8
            case 0x0A:
                register.A = cpuROMLoad(register.BC)
                return 8
            case 0x1A:
                register.A = cpuROMLoad(register.DE)
                return 8
            case 0xFA:
                register.A = cpuLoadImmediate16BitMemory()
                return 16
            case 0x7F:
                cpuLoadImmediate8BitMemory(register.A)
                return 8

            /* Write register to memory */
            case 0x70:
                cpuLoadRegisterToMemory(register.HL, register.B)
                return 8
            case 0x71:
                cpuLoadRegisterToMemory(register.HL, register.C)
                return 8
            case 0x72:
                cpuLoadRegisterToMemory(register.HL, register.D)
                return 8
            case 0x73:
                cpuLoadRegisterToMemory(register.HL, register.E)
                return 8
            case 0x74:
                cpuLoadRegisterToMemory(register.HL, register.H)
                return 8
            case 0x75:
                cpuLoadRegisterToMemory(register.HL, register.L)
                return 8
            case 0x36:
                cpuLoadImmediate8BitMemory(register.HL)
                return 12

            /* LD n, A : Put value A into n*/
            case 0x47:
                register.B = cpuRegisterLoad(register.A)
                return 4
            case 0x4F:
                register.C = cpuRegisterLoad(register.A)
                return 4
            case 0x57:
                register.D = cpuRegisterLoad(register.A)
                return 4
            case 0x5F:
                register.E = cpuRegisterLoad(register.A)
                return 4
            case 0x67:
                register.H = cpuRegisterLoad(register.A)
                return 4
            case 0x6F:
                register.L = cpuRegisterLoad(register.A)
                return 4

            /* Load A into memory address */
            case 0x02:
                cpuLoadRegisterToMemory(register.BC, register.A)
                return 8
            case 0x12:
                cpuLoadRegisterToMemory(register.DE, register.A)
                return 8
            case 0x77:
                cpuLoadRegisterToMemory(register.HL, register.A)
                return 8
            case 0xEA:
                cpuLoadRegisterToImmediateByte(register.A)
                return 16

            /* LD A,(0xFF00 + C) */
            case 0xF2:
                register.A = cpuROMLoad(0xFF00 + register.C)
                return 8

            /* LD (0xFF00 + C), A */
            case 0xE2:
                memoryManager.writeMemory(0xFF00 + register.C, register.A)
                return 8

            /* Load from memory into A, decrement/increment memory */
            case 0x3A:
                register.A = cpuROMLoad(register.HL)
                register.HL = cpu16BitDec(register.HL)
                return 8
            case 0x2A:
                register.A = cpuROMLoad(register.HL)
                register.HL = cpu16BitInc(register.HL)
                return 8

            /* Load from A into memory increment/decrement register */
            case 0x22:
                cpuLoadRegisterToMemory(register.HL, register.A)
                register.HL = cpu16BitInc(register.HL)
                return 8

            case 0xE0:
                int n = memoryManager.readMemory(register.pc)
                register.pc++
                int address = 0xFF00 + n
                memoryManager.writeMemory(address, register.A)
                return 12

            case 0xF0:
                int n = memoryManager.readMemory(register.pc)
                register.pc++
                register.A = memoryManager.readMemory(0xFF00 + n)
                return 12

            /* 16 bit loads */
            case 0x01:
                register.BC = cpu16BitImmediateLoad()
                return 12
            case 0x11:
                register.DE = cpu16BitImmediateLoad()
                return 12
            case 0x21:
                register.HL = cpu16BitImmediateLoad()
                return 12
            case 0x31:
                register.sp = cpu16BitImmediateLoad()
                return 12
            case 0xF9:
                register.sp = register.HL
                return 8
            case 0xF8:
                cpu16BitLDHL()
                return 12
            case 0x08:
                int nn = memoryManager.readWord()
                register.pc += 2
                memoryManager.writeMemory(nn, register.getSPLow())
                nn++
                memoryManager.writeMemory(nn, register.getSPHigh())
                return 20

            /* push */
            case 0xF5:
                memoryManager.push(register.AF)
                return 16
            case 0xC5:
                memoryManager.push(register.BC)
                return 16
            case 0xD5:
                memoryManager.push(register.DE)
                return 16
            case 0xE5:
                memoryManager.push(register.HL)
                return 16

            /* pop */
            case 0xF1:
                register.AF = memoryManager.pop()
                return 12
            case 0xC1:
                register.BC = memoryManager.pop()
                return 12
            case 0xD1:
                register.DE = memoryManager.pop()
                return 12
            case 0xE1:
                register.HL = memoryManager.pop()
                return 12

            /* 8-bit Add */
            case 0x87:
                register.A = cpu8BitAdd(register.A, register.A, false, false)
                return 4
            case 0x80:
                register.A = cpu8BitAdd(register.A, register.B, false, false)
                return 4
            case 0x81:
                register.A = cpu8BitAdd(register.A, register.C, false, false)
                return 4
            case 0x82:
                register.A = cpu8BitAdd(register.A, register.D, false, false)
                return 4
            case 0x83:
                register.A = cpu8BitAdd(register.A, register.E, false, false)
                return 4
            case 0x84:
                register.A = cpu8BitAdd(register.A, register.H, false, false)
                return 4
            case 0x85:
                register.A = cpu8BitAdd(register.A, register.L, false, false)
                return 4
            case 0x86:
                register.A = cpu8BitAdd(register.A, memoryManager.readMemory(register.HL), false, false)
                return 8
            case 0xC6:
                register.A = cpu8BitAdd(register.A, 0, true, false)
                return 8

            /* 8-bit Add with Carry */
            case 0x8F:
                register.A = cpu8BitAdd(register.A, register.A, false, true)
                return 4
            case 0x88:
                register.A = cpu8BitAdd(register.A, register.B, false, true)
                return 4
            case 0x89:
                register.A = cpu8BitAdd(register.A, register.C, false, true)
                return 4
            case 0x8A:
                register.A = cpu8BitAdd(register.A, register.D, false, true)
                return 4
            case 0x8B:
                register.A = cpu8BitAdd(register.A, register.E, false, true)
                return 4
            case 0x8C:
                register.A = cpu8BitAdd(register.A, register.H, false, true)
                return 4
            case 0x8D:
                register.A = cpu8BitAdd(register.A, register.L, false, true)
                return 4
            case 0x8E:
                register.A = cpu8BitAdd(register.A, memoryManager.readMemory(register.HL), false, true)
                return 8
            case 0xCE:
                register.A = cpu8BitAdd(register.A, 0, true, true)
                return 8

            /* 8-bit sub */
            case 0x97:
                register.A = cpu8BitSub(register.A, register.A, false, false)
                return 4
            case 0x90:
                register.A = cpu8BitSub(register.A, register.B, false, false)
                return 4
            case 0x91:
                register.A = cpu8BitSub(register.A, register.C, false, false)
                return 4
            case 0x92:
                register.A = cpu8BitSub(register.A, register.D, false, false)
                return 4
            case 0x93:
                register.A = cpu8BitSub(register.A, register.E, false, false)
                return 4
            case 0x94:
                register.A = cpu8BitSub(register.A, register.H, false, false)
                return 4
            case 0x95:
                register.A = cpu8BitSub(register.A, register.L, false, false)
                return 4
            case 0x96:
                register.A = cpu8BitSub(register.A, memoryManager.readMemory(register.HL), false, false)
                return 8
            case 0xD6:
                register.A = cpu8BitSub(register.A, 0, true, false)
                return 8

            /* 8-bit sub with carry */


            ////////////////////////////////////////////////////
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

    private int cpu16BitImmediateLoad() {
        int nn = memoryManager.readWord(register.pc)
        register.pc += 2
        return nn
    }

    private void cpu16BitLDHL() {
        /* If problems occur, double check the implementation of this method */
        int n = memoryManager.readMemory(register.sp)
        register.pc++
        int result = register.sp + n

        register.HL = result & 0xFFFF

        register.clearZ()
        register.clearN()

        if(result > 0xFFFF) {
            register.setC()
        }else {
            register.clearC()
        }

        if((register.sp & 0xF) + (n & 0xF) > 0xF){
            register.setH()
        }else {
            register.clearH()
        }
    }


    private int cpu8BitLoad() {
        int n = memoryManager.readMemory(register.pc)
        register.pc++
        return n
    }

    private int cpuLoadImmediate16BitMemory() {
        int nn = memoryManager.readWord()
        register.pc += 2 /* Memory is stored in bytes and 1 word (2 bytes) are read */
        int n = memoryManager.readMemory(nn)
        return n
    }

    private void cpuLoadRegisterToImmediateByte(int reg) {
        int nn = memoryManager.readWord()
        register.pc += 2
        memoryManager.writeMemory(nn, reg)
    }

    private def cpuRegisterLoad(int register2) {
        return register2
    }

    private void cpuLoadImmediate8BitMemory(int destination) {
        int data = memoryManager.readMemory(register.pc)
        register.pc++
        memoryManager.writeMemory(destination, data)
    }

    private void cpuLoadRegisterToMemory(int address, int data) {
        memoryManager.writeMemory(address, data)
    }

    private int cpuROMLoad(int address) {
        return memoryManager.readMemory(address)
    }

    private int cpu16BitDec(int reg) {
        return (reg-1)
    }

    private int cpu16BitInc(int reg) {
        return (reg+1)
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
