package org.topaz.cpu

import org.topaz.cpu.Register
import org.topaz.InterruptHandler
import org.topaz.MemoryManager
import org.topaz.util.BitUtil

class CPU{
    Register register
    MemoryManager memoryManager

    boolean isHalted = false
    boolean interruptsDisabled = false
    boolean interruptsEnabled = false

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
                register.@C = cpu8BitLoad()
                return 8
            case 0x16:
                register.D = cpu8BitLoad()
                return 8
            case 0x1E:
                register.E = cpu8BitLoad()
                return 8
            case 0x26:
                register.@H = cpu8BitLoad()
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
                register.@C = cpuRegisterLoad(register.B)
                return 4
            case 0x49:
                register.@C = cpuRegisterLoad(register.C)
                return 4
            case 0x4A:
                register.@C = cpuRegisterLoad(register.D)
                return 4
            case 0x4B:
                register.@C = cpuRegisterLoad(register.E)
                return 4
            case 0x4C:
                register.@C = cpuRegisterLoad(register.H)
                return 4
            case 0x4D:
                register.@C = cpuRegisterLoad(register.L)
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
                register.@H = cpuRegisterLoad(register.B)
                return 4
            case 0x61:
                register.@H = cpuRegisterLoad(register.C)
                return 4
            case 0x62:
                register.@H = cpuRegisterLoad(register.D)
                return 4
            case 0x63:
                register.@H = cpuRegisterLoad(register.E)
                return 4
            case 0x64:
                register.@H = cpuRegisterLoad(register.H)
                return 4
            case 0x65:
                register.@H = cpuRegisterLoad(register.L)
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
                register.@C = cpuROMLoad(register.HL)
                return 8
            case 0x56:
                register.D = cpuROMLoad(register.HL)
                return 8
            case 0x5E:
                register.E = cpuROMLoad(register.HL)
                return 8
            case 0x66:
                register.@H = cpuROMLoad(register.HL)
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
                register.@C = cpuRegisterLoad(register.A)
                return 4
            case 0x57:
                register.D = cpuRegisterLoad(register.A)
                return 4
            case 0x5F:
                register.E = cpuRegisterLoad(register.A)
                return 4
            case 0x67:
                register.@H = cpuRegisterLoad(register.A)
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
            case 0x9F:
                register.A = cpu8BitSub(register.A, register.A, false, true)
                return 4
            case 0x98:
                register.A = cpu8BitSub(register.A, register.B, false, true)
                return 4
            case 0x99:
                register.A = cpu8BitSub(register.A, register.C, false, true)
                return 4
            case 0x9A:
                register.A = cpu8BitSub(register.A, register.D, false, true)
                return 4
            case 0x9B:
                register.A = cpu8BitSub(register.A, register.E, false, true)
                return 4
            case 0x9C:
                register.A = cpu8BitSub(register.A, register.H, false, true)
                return 4
            case 0x9D:
                register.A = cpu8BitSub(register.A, register.L, false, true)
                return 4
            case 0x9E:
                register.A = cpu8BitSub(register.A, memoryManager.readMemory(register.HL), false, true)
                return 8

            /* Logical AND */
            case 0xA7:
                register.A = cpu8BitAND(register.A, register.A, false)
                return 4
            case 0xA0:
                register.A = cpu8BitAND(register.A, register.B, false)
                return 4
            case 0xA1:
                register.A = cpu8BitAND(register.A, register.C, false)
                return 4
            case 0xA2:
                register.A = cpu8BitAND(register.A, register.D, false)
                return 4
            case 0xA3:
                register.A = cpu8BitAND(register.A, register.E, false)
                return 4
            case 0xA4:
                register.A = cpu8BitAND(register.A, register.H, false)
                return 4
            case 0xA5:
                register.A = cpu8BitAND(register.A, register.L, false)
                return 4
            case 0xA6:
                register.A = cpu8BitAND(register.A, memoryManager.readMemory(register.HL), false)
                return 8
            case 0xE6:
                register.A = cpu8BitAND(register.A, 0, true)
                return 8

            /* Logical OR */
            case 0xB7:
                register.A = cpu8BitOR(register.A, register.A, false)
                return 4
            case 0xB0:
                register.A = cpu8BitOR(register.A, register.B, false)
                return 4
            case 0xB1:
                register.A = cpu8BitOR(register.A, register.C, false)
                return 4
            case 0xB2:
                register.A = cpu8BitOR(register.A, register.D, false)
                return 4
            case 0xB3:
                register.A = cpu8BitOR(register.A, register.E, false)
                return 4
            case 0xB4:
                register.A = cpu8BitOR(register.A, register.H, false)
                return 4
            case 0xB5:
                register.A = cpu8BitOR(register.A, register.L, false)
                return 4
            case 0xB6:
                register.A = cpu8BitOR(register.A, memoryManager.readMemory(register.HL), false)
                return 8
            case 0xF6:
                register.A = cpu8BitOR(register.A, 0, true)
                return 8

            /* Logical XOR */
            case 0xAF:
                register.A = cpu8BitXOR(register.A, register.A, false)
                return 4
            case 0xA8:
                register.A = cpu8BitXOR(register.A, register.B, false)
                return 4
            case 0xA9:
                register.A = cpu8BitXOR(register.A, register.C, false)
                return 4
            case 0xAA:
                register.A = cpu8BitXOR(register.A, register.D, false)
                return 4
            case 0xAB:
                register.A = cpu8BitXOR(register.A, register.E, false)
                return 4
            case 0xAC:
                register.A = cpu8BitXOR(register.A, register.H, false)
                return 4
            case 0xAD:
                register.A = cpu8BitXOR(register.A, register.L, false)
                return 4
            case 0xAE:
                register.A = cpu8BitXOR(register.A, memoryManager.readMemory(register.HL), false)
                return 8
            case 0xEE:
                register.A = cpu8BitXOR(register.A, 0, true)
                return 8

            /* Compares */
            case 0xBF:
                cpuCompare(register.A, register.A, false)
                return 4
            case 0xB8:
                cpuCompare(register.A, register.B, false)
                return 4
            case 0xB9:
                cpuCompare(register.A, register.C, false)
                return 4
            case 0xBA:
                cpuCompare(register.A, register.D, false)
                return 4
            case 0xBB:
                cpuCompare(register.A, register.E, false)
                return 4
            case 0xBC:
                cpuCompare(register.A, register.H, false)
                return 4
            case 0xBD:
                cpuCompare(register.A, register.L, false)
                return 4
            case 0xBE:
                cpuCompare(register.A, memoryManager.readMemory(register.HL), false)
                return 8
            case 0xFE:
                cpuCompare(register.A, 0, true)
                return 8

            /* Increment */
            case 0x3C:
                register.A = cpu8BitInc(register.A)
                return 4
            case 0x04:
                register.B = cpu8BitInc(register.B)
                return 4
            case 0x0C:
                register.@C = cpu8BitInc(register.C)
                return 4
            case 0x14:
                register.D = cpu8BitInc(register.D)
                return 4
            case 0x1C:
                register.E = cpu8BitInc(register.E)
                return 4
            case 0x24:
                register.@H = cpu8BitInc(register.H)
                return 4
            case 0x2C:
                register.L = cpu8BitInc(register.L)
                return 4
            case 0x34:
                cpuIncMemory(register.HL)
                return 12

            /* Decrement */
            case 0x3D:
                register.A = cpu8BitDec(register.A)
                return 4
            case 0x05:
                register.B = cpu8BitDec(register.B)
                return 4
            case 0x0D:
                register.@C = cpu8BitDec(register.C)
                return 4
            case 0x15:
                register.D = cpu8BitDec(register.D)
                return 4
            case 0x1D:
                register.E = cpu8BitDec(register.E)
                return 4
            case 0x25:
                register.@H = cpu8BitDec(register.H)
                return 4
            case 0x2D:
                register.L = cpu8BitDec(register.L)
                return 4
            case 0x35:
                cpuDecMemory(register.HL)
                return 12

            /* 16-bit add */
            case 0x09:
                register.HL = cpu16BitAdd(register.HL, register.BC)
                return 8
            case 0x19:
                register.HL = cpu16BitAdd(register.HL, register.DE)
                return 8
            case 0x29:
                register.HL = cpu16BitAdd(register.HL, register.HL)
                return 8
            case 0x39:
                register.HL = cpu16BitAdd(register.HL, register.sp)
                return 8

            /* Add n to stack pointer */
            case 0xE8:
                cpu8BitSPAdd()
                return 16

            /* 16 bit inc */ 
            case 0x03:
                register.BC = cpu16BitInc(register.BC)
                return 8
            case 0x13:
                register.DE = cpu16BitInc(register.DE)
                return 8
            case 0x23:
                register.HL = cpu16BitInc(register.HL)
                return 8
            case 0x33:
                register.sp = cpu16BitInc(register.sp)
                return 8

            /* 16 bit DEC */
            case 0x0B:
                register.BC = cpu16BitDec(register.BC)
                return 8
            case 0x1B:
                register.DE = cpu16BitDec(register.DE)
                return 8
            case 0x2B:
                register.HL = cpu16BitDec(register.HL)
                return 8
            case 0x3B:
                register.sp = cpu16BitDec(register.sp)
                return 8

            /* DAA */
            case 0x27:
                cpuDAA()
                return 4

            /* Miscellaneous */
            case 0x2F:
                register.A = ~register.A
                register.setN()
                register.setH()
                return 4

            case 0x37:
                register.setC()
                register.clearN()
                register.clearH()
                return 4

            case 0x76:
                this.isHalted = true
                return 4

            case 0x10: //Stop
                register.pc++
                return 4

            case 0xF3:
                this.interruptsDisabled = true
                return 4

            case 0xFB:
                this.interruptsEnabled = true
                return 4

            /* Rotates and shifts */
            case 0x07:
                register.A = cpuRLC(register.A)
                return 4

            case 0x17:
                register.A = cpuRL(register.A)
                return 4

            case 0x0F:
                register.A = cpuRRC(register.A)
                return 4

            case 0x1F:
                register.A = cpuRR(register.A)
                return 4
            ////////////////////////////////////////////////////


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
            /* Swaps */
            case 0x37:
                register.A = cpuSwapNibbles(register.A)
                return 8
            case 0x30:
                register.B = cpuSwapNibbles(register.B)
                return 8
            case 0x31:
                register.@C = cpuSwapNibbles(register.C)
                return 8
            case 0x32:
                register.D = cpuSwapNibbles(register.D)
                return 8
            case 0x33:
                register.E = cpuSwapNibbles(register.E)
                return 8
            case 0x34:
                register.@H = cpuSwapNibbles(register.H)
                return 8
            case 0x35:
                register.L = cpuSwapNibbles(register.L)
                return 8

            /* Rotate left through carry */ 
            case 0x07:
                register.A = cpuRLC(register.A)
                return 8
            case 0x00:
                register.B = cpuRLC(register.B)
                return 8
            case 0x01:
                register.@C = cpuRLC(register.C)
                return 8
            case 0x02:
                register.D = cpuRLC(register.D)
                return 8
            case 0x03:
                register.E = cpuRLC(register.E)
                return 8
            case 0x04:
                register.@H = cpuRLC(register.H)
                return 8
            case 0x05:
                register.L = cpuRLC(register.L)
                return 8
            case 0x06:
                cpuRLCMemory(register.HL)
                return 16

            /* rotate left */
            case 0x17:
                register.A = cpuRL(register.A)
                return 8
            case 0x10:
                register.B = cpuRL(register.B)
                return 8
            case 0x11:
                register.@C = cpuRL(register.C)
                return 8
            case 0x12:
                register.D = cpuRL(register.D)
                return 8
            case 0x13:
                register.E = cpuRL(register.E)
                return 8
            case 0x14:
                register.@H = cpuRL(register.H)
                return 8
            case 0x15:
                register.L = cpuRL(register.A)
                return 8
            case 0x16:
                cpuRLMemory(register.HL)
                return 16

            /* Rotate right through carry */
            case 0x0F:
                register.A = cpuRRC(register.A)
                return 8
            case 0x08:
                register.B = cpuRRC(register.B)
                return 8
            case 0x09:
                register.@C = cpuRRC(register.C)
                return 8
            case 0x0A:
                register.D = cpuRRC(register.D)
                return 8
            case 0x0B:
                register.E = cpuRRC(register.E)
                return 8
            case 0x0C:
                register.@H = cpuRRC(register.H)
                return 8
            case 0x0D:
                register.L = cpuRRC(register.L)
                return 8
            case 0x0E:
                cpuRRCMemory(register.HL)
                return 16

            /* rotate right */
            case 0x1F:
                register.A = cpuRR(register.A)
                return 8
            case 0x18:
                register.B = cpuRR(register.B)
                return 8
            case 0x19:
                register.@C = cpuRR(register.C)
                return 8
            case 0x1A:
                register.D = cpuRR(register.D)
                return 8
            case 0x1B:
                register.E = cpuRR(register.E)
                return 8
            case 0x1C:
                register.@H = cpuRR(register.H)
                return 8
            case 0x1D:
                register.L = cpuRR(register.L)
                return 8
            case 0x1E:
                cpuRRMemory(register.HL)
                return 16

            /* shift left */
            case 0x27:
                register.A = cpuSLA(register.A)
                return 8
            case 0x20:
                register.B = cpuSLA(register.B)
                return 8
            case 0x21:
                register.@C = cpuSLA(register.C)
                return 8
            case 0x22:
                register.D = cpuSLA(register.D)
                return 8
            case 0x23:
                register.E = cpuSLA(register.E)
                return 8
            case 0x24:
                register.@H = cpuSLA(register.H)
                return 8
            case 0x25:
                register.L = cpuSLA(register.L)
                return 8
            case 0x26:
                cpuSLAMemory()
                return 16
                
                /* shift right into carry */
                case 0x2F:
                register.A = cpuSRA(register.A) 
                return 8
                case 0x28:
                register.B = cpuSRA(register.B) 
                return 8
                case 0x29:
                register.@C = cpuSRA(register.C) 
                return 8
                case 0x2A:
                register.D = cpuSRA(register.D) 
                return 8
                case 0x2B:
                register.E = cpuSRA(register.E) 
                return 8
                case 0x2C:
                register.@H = cpuSRA(register.H) 
                return 8
                case 0x2D:
                register.L = cpuSRA(register.L) 
                return 8
                case 0x2E:
                cpuSRAMemory(register.HL) 
                return 16
            default:
                def hexCode = java.lang.String.format("0x%2X", opcode)
                throw new Exception("Unrecognized extended opcode: " + hexCode)
        }
    }

    //STOLEN! :-)
    private void cpuDAA() {
        if(!register.isN()) {
            if(register.isC() || register.A > 0x99) {
                register.A = register.A + 0x60
                register.setC()
            }

            if(register.isH() || ((register.A & 0x0F) > 0x09)) {
                register.A = register.A + 0x6
            }else {
                if(register.isC()) {
                    register.A = register.A - 0x60
                }
                if(register.isH()) {
                    register.A = register.A - 0x6
                }
            }
        }

        register.setZ(register.A == 0)
        register.clearH()
    }

    private int cpuSRA(int reg) {
        boolean isLSBSet = BitUtil.isSet(reg, 0)
        boolean isMSBSet = BitUtil.isSet(reg, 7)

        reg = (reg >> 1) & 0xFF

        if(isMSBSet) {
            reg = BitUtil.setBit(reg, 7)
        }

        register.clearN()
        register.clearH()
        register.setC(isLSBSet)
        register.setZ(reg == 0)

        return reg
    }

    private void cpuSRAMemory(int address) {
        int reg = memoryManager.readMemory(address)

        boolean isLSBSet = BitUtil.isSet(reg, 0)
        boolean isMSBSet = BitUtil.isSet(reg, 7)

        reg = (reg >> 1) & 0xFF

        if(isMSBSet) {
            reg = BitUtil.setBit(reg, 7)
        }

        register.clearN()
        register.clearH()
        register.setC(isLSBSet)
        register.setZ(reg == 0)
        
        memoryManager.writeMemory(address, reg)
    }

    private int cpuSLA(int reg) {
        reg = (reg << 1) & 0xFF
        register.clearN()
        register.clearH()
        register.setC(BitUtil.isSet(reg, 7))
        register.setZ(reg == 0)

        return reg
    }

    private void cpuSLAMemory(int address) {
        int reg = memoryManager.readMemory(address)
        reg = (reg << 1) & 0xFF
        register.clearN()
        register.clearH()
        register.setC(BitUtil.isSet(reg, 7))
        register.setZ(reg == 0)

        memoryManager.writeMemory(address, reg)
    }

    private int cpuRLC(int reg) {
        int n = reg & 0xFF
        register.A = (reg << 1 | reg >> 7) & 0xFF
        register.setZ(reg == 0)
        register.clearN()
        register.clearH()
        register.setC(BitUtil.isSet(n, 7))

        return reg
    }

    private void cpuRLCMemory(int address) {
        int reg = memoryManager.readMemory(address)

        boolean isMSBSet = BitUtil.isSet(reg, 7)

        reg = (reg << 1) & 0xFF

        register.clearN()
        register.clearH()

        if(isMSBSet) {
            register.setC()
            reg = BitUtil.setBit(reg, 0)
        }

        register.setZ(reg == 0)
        memoryManager.writeMemory(address, reg)
    }

    private int cpuRL(int reg) {
        boolean carrySet = register.isC()
        boolean isMSBSet = BitUtil.isSet(reg, 7)

        reg = (reg << 1) & 0xFF

        register.clearH()
        register.clearN()
        register.setC(isMSBSet)

        if(carrySet) {
            reg = BitUtil.setBit(reg, 0)
        }

        register.setZ(reg == 0)

        return reg
    }

    private void cpuRLMemory(int address) {
        int reg = memoryManager.readMemory(address)

        boolean isCarrySet = register.isC()
        boolean isMSBSet = BitUtil.isSet(reg, 7)

        reg = (reg << 1) & 0xFF

        register.clearH()
        register.clearN()
        register.setC(isMSBSet)

        if(isCarrySet) {
            reg = BitUtil.setBit(reg, 0)
        }

        register.setZ(reg == 0)
        memoryManager.writeMemory(address, reg)
    }

    private int cpuRRC(int reg) {
        boolean isLSBSet = BitUtil.isSet(reg, 0)

        register.clearAllFlags()

        reg = (reg >> 1) & 0xFF

        if(isLSBSet) {
            register.setC()
            reg = BitUtil.setBit(reg, 7)
        }

        if(reg == 0) {
            register.setZ()
        }

        return reg
    }

    private void cpuRRCMemory(int address) {
        int reg = memoryManager.readMemory(address)

        boolean isLSBSet = BitUtil.isSet(reg, 0)

        reg = (reg >> 1) & 0xFF

        register.clearN()
        register.clearH()

        if(isLSBSet) {
            register.setC()
            reg = BitUtil.setBit(reg, 7)
        }

        register.setZ(reg == 0)
        memoryManager.writeMemory(address, reg)
    }

    private int cpuRR(int reg) {
        boolean carrySet = register.isC()
        boolean isLSBSet = BitUtil.isSet(reg, 0)

        reg = (reg >> 1) & 0xFF

        register.clearH()
        register.clearN()
        register.setC(isLSBSet)

        if(carrySet) {
            reg = BitUtil.setBit(reg, 7)
        }

        register.setZ(reg == 0)

        return reg
    }

    private void cpuRRMemory(int address) {
        int reg = memoryManager.readMemory(address)

        boolean isCarrySet = register.isC()
        boolean isLSBSet = BitUtil.isSet(reg, 0)

        reg = (reg >> 1) & 0xFF

        register.clearN()
        register.clearH()
        register.setC(isLSBSet)

        if(isCarrySet) {
            reg = BitUtil.setBit(reg, 7)
        }

        register.setZ(reg == 0)
        memoryManager.writeMemory(address, reg)
    }

    private int cpuSwapNibbles(int n){
        n = (((n & 0xF0) >> 4) | ((n & 0x0F) << 4))

        register.clearAllFlags()
        register.setZ(n == 0)
        return n
    }

    private int cpu16BitImmediateLoad() {
        int nn = memoryManager.readWord(register.pc)
        register.pc += 2
        return nn
    }

    private void cpuCompare(int A, int val, boolean useImmediate) {
        int n = 0

        if(useImmediate) {
            n = memoryManager.readMemory(register.pc)
            register.pc++
        }else {
            n = val
        }

        register.setZ(A == n)
        register.setC(A < n)
        register.setH((n & 0x0F) > (A & 0x0F))
        register.setN(true)
    }

    private int cpu8BitInc(int n){
        def initialN = n
        n++
        register.setZ(n == 0)
        register.setN(false)
        register.setH(((initialN & 0x0F) + (1 & 0x0F)) > 0x0F)
        return n
    }

    private void cpuIncMemory(int address) {
        int n = memoryManager.readMemory(address)
        int initialN = n
        n++
        memoryManager.writeMemory(address, n)

        register.setZ(n == 0)
        register.setN(false)
        register.setH(((initialN & 0x0F) + (1 & 0x0F)) > 0x0F)
    }

    private int cpu8BitDec(int n){
        int initialN = n
        n--
        register.setZ(n == 0)
        register.setN(true)
        register.setH((initialN & 0x0F) == 0)
        return n
    }

    private void cpuDecMemory(int address) {
        int n = memoryManager.readMemory(address)
        int initialN = n
        n--
        memoryManager.writeMemory(address, n)

        register.setZ(n == 0)
        register.setN(true)
        register.setH((initialN & 0x0F) == 0)
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

    private int cpu8BitAND(int reg, int value, boolean useImmediate) {
        int n = 0

        if(useImmediate) {
            n = memoryManager.readMemory(register.pc)
            register.pc++
        }else {
            n = value
        }

        reg = reg & n

        register.clearN()
        register.setH()
        register.clearC()

        if(reg == 0x0) {
            register.setZ()
        }
        return reg
    }

    private int cpu8BitOR(int reg, int value, boolean useImmediate) {
        int n = 0

        if(useImmediate) {
            n = memoryManager.readMemory(register.pc)
            register.pc++
        }else {
            n = value
        }

        reg = reg | n

        register.clearN()
        register.clearH()
        register.clearC()

        if(reg == 0) {
            register.setZ()
        }

        return reg
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

    private int cpu16BitAdd(int reg, int n) {
        int initialHL = reg
        reg = reg + n

        register.clearN()

        /*
         * The half-carry is set if an overflow occurs from bit position 11. If
         * the initial value of the first 12 bits are greater than the 12 bits
         * of the result, it means that the result overflowed the bit at
         * position 11 and is therefore a greater value, i.e, a half-carry
         * occurred. Otherwise, the value of the first 12 bits will be greater
         * than the value of the initial 12 bits meaning that no overflow
         * occurred.
         */
        register.setH((initialHL & 0xFFF) > (reg & 0xFFF))
        register.setC(reg > 0xFFFF)

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

    private void cpu8BitSPAdd() {
        int n = memoryManager.readMemory(register.pc)
        int result = (register.pc + n) & 0xFFFF
        register.pc = result

        register.clearZ()
        register.clearN()

        /*
         * To understand the logic behind setting the carry and half carry flag,
         * see the following stackoverflow questions.
         * 
         * https://stackoverflow.com/questions/62006764/how-is-xor-applied-when-determining-carry
         * https://stackoverflow.com/questions/20494087/is-it-possible-to-write-a-function-adding-two-integers-without-control-flow-and/20500295#20500295
         * https://stackoverflow.com/questions/9070937/adding-two-numbers-without-operator-clarification
         */

        register.setC(((register.pc ^ n ^ result) & 0x100) != 0)
        register.setH(((register.pc ^ n ^ result) & 0x10) != 0)
    }

    private int cpu8BitXOR(int reg, int value, boolean useImmediate) {

        int n = 0

        if(useImmediate) {
            n = memoryManager.readMemory(register.pc)
            register.pc++
        }else {
            n = value
        }

        reg = reg ^ n

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
