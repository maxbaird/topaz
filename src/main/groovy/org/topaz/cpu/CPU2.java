package org.topaz.cpu;

import org.topaz.MemoryManager;
import org.topaz.util.BitUtil;
import org.topaz.debug.StateDumper;

public class CPU2 {
    Register2 register;
    MemoryManager memoryManager;
    StateDumper dumper;

    public boolean isHalted = false;
    public boolean interruptMaster = false;
    public boolean pendingInterruptEnabled = false;
    
    public CPU2(MemoryManager memoryManager, Register2 register, StateDumper dumper) {
        this.memoryManager = memoryManager;
        this.register = register;
        this.dumper = dumper;
    }

    public int executeNextOpcode(int n) {
        int cycles = 0;
        int opcode = memoryManager.readMemory(register.pc);

        // def hexCode = java.lang.String.format("0x%02X", opcode);
        // boolean display = (n >= Topaz.executionStart && n <= Topaz.executionEnd) ?
        // true : false

        register.pc++;
        // def extendedOpcode = (opcode == 0xCB) ? memoryManager.readMemory(register.pc)
        // : 0x0

        cycles = executeOpcode(opcode);
//        try {
//        }catch(Exception e) {
//            println e.message
//            System.exit(1);
//        }

//        if(display) {
//            def exOpcode = String.format("0x%02X", extendedOpcode);
//            //dumper.dump(n, hexCode, exOpcode, cycles, '/tmp/' + n + '.topaz');
//        }
        return cycles;
    }

    private int executeOpcode(int opcode) {
        int n;
        switch(opcode){
            /* No-op */
            case 0x00:
                return 4;

            /* 8-Bit Loads */
            case 0x06:
                register.B = cpu8BitLoad();
                return 8;
            case 0x0E:
                register.C = cpu8BitLoad();
                return 8;
            case 0x16:
                register.D = cpu8BitLoad();
                return 8;
            case 0x1E:
                register.E = cpu8BitLoad();
                return 8;
            case 0x26:
                register.H = cpu8BitLoad();
                return 8;
            case 0x2E:
                register.L = cpu8BitLoad();
                return 8;

            /* Register Loads */
            case 0x7F:
                register.A = cpuRegisterLoad(register.A);
                return 4;
            case 0x78:
                register.A = cpuRegisterLoad(register.B);
                return 4;
            case 0x79:
                register.A = cpuRegisterLoad(register.C);
                return 4;
            case 0x7A:
                register.A = cpuRegisterLoad(register.D);
                return 4;
            case 0x7B:
                register.A = cpuRegisterLoad(register.E);
                return 4;
            case 0x7C:
                register.A = cpuRegisterLoad(register.H);
                return 4;
            case 0x7D:
                register.A = cpuRegisterLoad(register.L);
                return 4;
            case 0x40:
                register.B = cpuRegisterLoad(register.B);
                return 4;
            case 0x41:
                register.B = cpuRegisterLoad(register.C);
                return 4;
            case 0x42:
                register.B = cpuRegisterLoad(register.D);
                return 4;
            case 0x43:
                register.B = cpuRegisterLoad(register.E);
                return 4;
            case 0x44:
                register.B = cpuRegisterLoad(register.H);
                return 4;
            case 0x45:
                register.B = cpuRegisterLoad(register.L);
                return 4;
            case 0x48:
                register.C = cpuRegisterLoad(register.B);
                return 4;
            case 0x49:
                register.C = cpuRegisterLoad(register.C);
                return 4;
            case 0x4A:
                register.C = cpuRegisterLoad(register.D);
                return 4;
            case 0x4B:
                register.C = cpuRegisterLoad(register.E);
                return 4;
            case 0x4C:
                register.C = cpuRegisterLoad(register.H);
                return 4;
            case 0x4D:
                register.C = cpuRegisterLoad(register.L);
                return 4;
            case 0x50:
                register.D = cpuRegisterLoad(register.B);
                return 4;
            case 0x51:
                register.D = cpuRegisterLoad(register.C);
                return 4;
            case 0x52:
                register.D = cpuRegisterLoad(register.D);
                return 4;
            case 0x53:
                register.D = cpuRegisterLoad(register.E);
                return 4;
            case 0x54:
                register.D = cpuRegisterLoad(register.H);
                return 4;
            case 0x55:
                register.D = cpuRegisterLoad(register.L);
                return 4;
            case 0x58:
                register.E = cpuRegisterLoad(register.B);
                return 4;
            case 0x59:
                register.E = cpuRegisterLoad(register.C);
                return 4;
            case 0x5A:
                register.E = cpuRegisterLoad(register.D);
                return 4;
            case 0x5B:
                register.E = cpuRegisterLoad(register.E);
                return 4;
            case 0x5C:
                register.E = cpuRegisterLoad(register.H);
                return 4;
            case 0x5D:
                register.E = cpuRegisterLoad(register.L);
                return 4;
            case 0x60:
                register.H = cpuRegisterLoad(register.B);
                return 4;
            case 0x61:
                register.H = cpuRegisterLoad(register.C);
                return 4;
            case 0x62:
                register.H = cpuRegisterLoad(register.D);
                return 4;
            case 0x63:
                register.H = cpuRegisterLoad(register.E);
                return 4;
            case 0x64:
                register.H = cpuRegisterLoad(register.H);
                return 4;
            case 0x65:
                register.H = cpuRegisterLoad(register.L);
                return 4;
            case 0x68:
                register.L = cpuRegisterLoad(register.B);
                return 4;
            case 0x69:
                register.L = cpuRegisterLoad(register.C);
                return 4;
            case 0x6A:
                register.L = cpuRegisterLoad(register.D);
                return 4;
            case 0x6B:
                register.L = cpuRegisterLoad(register.E);
                return 4;
            case 0x6C:
                register.L = cpuRegisterLoad(register.H);
                return 4;
            case 0x6D:
                register.L = cpuRegisterLoad(register.L);
                return 4;

            /* ROM Loads */
            case 0x7E:
                register.A = cpuROMLoad(register.getHL());
                return 8;
            case 0x46:
                register.B = cpuROMLoad(register.getHL());
                return 8;
            case 0x4E:
                register.C = cpuROMLoad(register.getHL());
                return 8;
            case 0x56:
                register.D = cpuROMLoad(register.getHL());
                return 8;
            case 0x5E:
                register.E = cpuROMLoad(register.getHL());
                return 8;
            case 0x66:
                register.H = cpuROMLoad(register.getHL());
                return 8;
            case 0x6E:
                register.L = cpuROMLoad(register.getHL());
                return 8;
            case 0x0A:
                register.A = cpuROMLoad(register.getBC());
                return 8;
            case 0x1A:
                register.A = cpuROMLoad(register.getDE());
                return 8;
            case 0xFA:
                register.A = cpuLoadImmediate16BitMemory();
                return 16;
//            case 0x7F:
//                cpuLoadImmediate8BitMemory(register.A);
//                return 8;

            case 0x3E:
                n = memoryManager.readMemory(register.pc);
                register.pc++;
                register.A = n;
                return 8;

            /* Write register to memory */
            case 0x70:
                cpuLoadRegisterToMemory(register.getHL(), register.B);
                return 8;
            case 0x71:
                cpuLoadRegisterToMemory(register.getHL(), register.C);
                return 8;
            case 0x72:
                cpuLoadRegisterToMemory(register.getHL(), register.D);
                return 8;
            case 0x73:
                cpuLoadRegisterToMemory(register.getHL(), register.E);
                return 8;
            case 0x74:
                cpuLoadRegisterToMemory(register.getHL(), register.H);
                return 8;
            case 0x75:
                cpuLoadRegisterToMemory(register.getHL(), register.L);
                return 8;
            case 0x36:
                cpuLoadImmediate8BitMemory(register.getHL());
                return 12;

            /* LD n, A : Put value A into n*/
            case 0x47:
                register.B = cpuRegisterLoad(register.A);
                return 4;
            case 0x4F:
                register.C = cpuRegisterLoad(register.A);
                return 4;
            case 0x57:
                register.D = cpuRegisterLoad(register.A);
                return 4;
            case 0x5F:
                register.E = cpuRegisterLoad(register.A);
                return 4;
            case 0x67:
                register.H = cpuRegisterLoad(register.A);
                return 4;
            case 0x6F:
                register.L = cpuRegisterLoad(register.A);
                return 4;

            /* Load A into memory address */
            case 0x02:
                cpuLoadRegisterToMemory(register.getBC(), register.A);
                return 8;
            case 0x12:
                cpuLoadRegisterToMemory(register.getDE(), register.A);
                return 8;
            case 0x77:
                cpuLoadRegisterToMemory(register.getHL(), register.A);
                return 8;
            case 0xEA:
                cpuLoadRegisterToImmediateByte(register.A);
                return 16;

            /* LD A,(0xFF00 + C) */
            case 0xF2:
                register.A = cpuROMLoad(0xFF00 + register.C);
                return 8;

            /* LD (0xFF00 + C), A */
            case 0xE2:
                memoryManager.writeMemory(0xFF00 + register.C, register.A);
                return 8;

            /* Load from memory into A, decrement/increment memory */
            case 0x3A:
                register.A = cpuROMLoad(register.getHL());
                register.setHL( cpu16BitDec(register.getHL()));
                return 8;
            case 0x2A:
                register.A = cpuROMLoad(register.getHL());
                register.setHL(cpu16BitInc(register.getHL()));
                return 8;

            /* Load from A into memory increment/decrement register */
            case 0x22:
                cpuLoadRegisterToMemory(register.getHL(), register.A);
                register.setHL(cpu16BitInc(register.getHL()));
                return 8;

            case 0xE0:
                n = memoryManager.readMemory(register.pc);
                register.pc++;
                int address = 0xFF00 + n;
                memoryManager.writeMemory(address, register.A);
                return 12;

            case 0xF0:
                n = memoryManager.readMemory(register.pc);
                register.pc++;
                register.A = memoryManager.readMemory(0xFF00 + n);
                return 12;

            /* 16 bit loads */
            case 0x01:
                register.setBC(cpu16BitImmediateLoad());
                return 12;
            case 0x11:
                register.setDE(cpu16BitImmediateLoad());
                return 12;
            case 0x21:
                register.setHL(cpu16BitImmediateLoad());
                return 12;
            case 0x31:
                register.sp = cpu16BitImmediateLoad();
                return 12;
            case 0xF9:
                register.sp = register.getHL();
                return 8;
            case 0xF8:
                cpu16BitLDHL();
                return 12;
            case 0x08:
                int nn = memoryManager.readWord();
                register.pc += 2;
                memoryManager.writeMemory(nn, register.getSPLow());
                nn++;
                memoryManager.writeMemory(nn, register.getSPHigh());
                return 20;

            /* push */
            case 0xF5:
                memoryManager.push(register.getAF());
                return 16;
            case 0xC5:
                memoryManager.push(register.getBC());
                return 16;
            case 0xD5:
                memoryManager.push(register.getDE());
                return 16;
            case 0xE5:
                memoryManager.push(register.getHL());
                return 16;

            /* pop */
            case 0xF1:
                register.setAF(memoryManager.pop());
                return 12;
            case 0xC1:
                register.setBC(memoryManager.pop());
                return 12;
            case 0xD1:
                register.setDE(memoryManager.pop());
                return 12;
            case 0xE1:
                register.setHL(memoryManager.pop());
                return 12;

            /* 8-bit Add */
            case 0x87:
                register.A = cpu8BitAdd(register.A, register.A, false, false);
                return 4;
            case 0x80:
                register.A = cpu8BitAdd(register.A, register.getBC(), false, false);
                return 4;
            case 0x81:
                register.A = cpu8BitAdd(register.A, register.C, false, false);
                return 4;
            case 0x82:
                register.A = cpu8BitAdd(register.A, register.D, false, false);
                return 4;
            case 0x83:
                register.A = cpu8BitAdd(register.A, register.E, false, false);
                return 4;
            case 0x84:
                register.A = cpu8BitAdd(register.A, register.H, false, false);
                return 4;
            case 0x85:
                register.A = cpu8BitAdd(register.A, register.L, false, false);
                return 4;
            case 0x86:
                register.A = cpu8BitAdd(register.A, memoryManager.readMemory(register.getHL()), false, false);
                return 8;
            case 0xC6:
                register.A = cpu8BitAdd(register.A, 0, true, false);
                return 8;

            /* 8-bit Add with Carry */
            case 0x8F:
                register.A = cpu8BitAdd(register.A, register.A, false, true);
                return 4;
            case 0x88:
                register.A = cpu8BitAdd(register.A, register.getBC(), false, true);
                return 4;
            case 0x89:
                register.A = cpu8BitAdd(register.A, register.C, false, true);
                return 4;
            case 0x8A:
                register.A = cpu8BitAdd(register.A, register.D, false, true);
                return 4;
            case 0x8B:
                register.A = cpu8BitAdd(register.A, register.E, false, true);
                return 4;
            case 0x8C:
                register.A = cpu8BitAdd(register.A, register.H, false, true);
                return 4;
            case 0x8D:
                register.A = cpu8BitAdd(register.A, register.L, false, true);
                return 4;
            case 0x8E:
                register.A = cpu8BitAdd(register.A, memoryManager.readMemory(register.getHL()), false, true);
                return 8;
            case 0xCE:
                register.A = cpu8BitAdd(register.A, 0, true, true);
                return 8;

            /* 8-bit sub */
            case 0x97:
                register.A = cpu8BitSub(register.A, register.A, false, false);
                return 4;
            case 0x90:
                register.A = cpu8BitSub(register.A, register.getBC(), false, false);
                return 4;
            case 0x91:
                register.A = cpu8BitSub(register.A, register.C, false, false);
                return 4;
            case 0x92:
                register.A = cpu8BitSub(register.A, register.D, false, false);
                return 4;
            case 0x93:
                register.A = cpu8BitSub(register.A, register.E, false, false);
                return 4;
            case 0x94:
                register.A = cpu8BitSub(register.A, register.H, false, false);
                return 4;
            case 0x95:
                register.A = cpu8BitSub(register.A, register.L, false, false);
                return 4;
            case 0x96:
                register.A = cpu8BitSub(register.A, memoryManager.readMemory(register.getHL()), false, false);
                return 8;
            case 0xD6:
                register.A = cpu8BitSub(register.A, 0, true, false);
                return 8;

            /* 8-bit sub with carry */
            case 0x9F:
                register.A = cpu8BitSub(register.A, register.A, false, true);
                return 4;
            case 0x98:
                register.A = cpu8BitSub(register.A, register.getBC(), false, true);
                return 4;
            case 0x99:
                register.A = cpu8BitSub(register.A, register.C, false, true);
                return 4;
            case 0x9A:
                register.A = cpu8BitSub(register.A, register.D, false, true);
                return 4;
            case 0x9B:
                register.A = cpu8BitSub(register.A, register.E, false, true);
                return 4;
            case 0x9C:
                register.A = cpu8BitSub(register.A, register.H, false, true);
                return 4;
            case 0x9D:
                register.A = cpu8BitSub(register.A, register.L, false, true);
                return 4;
            case 0x9E:
                register.A = cpu8BitSub(register.A, memoryManager.readMemory(register.getHL()), false, true);
                return 8;

            /* Logical AND */
            case 0xA7:
                register.A = cpu8BitAND(register.A, register.A, false);
                return 4;
            case 0xA0:
                register.A = cpu8BitAND(register.A, register.getBC(), false);
                return 4;
            case 0xA1:
                register.A = cpu8BitAND(register.A, register.C, false);
                return 4;
            case 0xA2:
                register.A = cpu8BitAND(register.A, register.D, false);
                return 4;
            case 0xA3:
                register.A = cpu8BitAND(register.A, register.E, false);
                return 4;
            case 0xA4:
                register.A = cpu8BitAND(register.A, register.H, false);
                return 4;
            case 0xA5:
                register.A = cpu8BitAND(register.A, register.L, false);
                return 4;
            case 0xA6:
                register.A = cpu8BitAND(register.A, memoryManager.readMemory(register.getHL()), false);
                return 8;
            case 0xE6:
                register.A = cpu8BitAND(register.A, 0, true);
                return 8;

            /* Logical OR */
            case 0xB7:
                register.A = cpu8BitOR(register.A, register.A, false);
                return 4;
            case 0xB0:
                register.A = cpu8BitOR(register.A, register.getBC(), false);
                return 4;
            case 0xB1:
                register.A = cpu8BitOR(register.A, register.C, false);
                return 4;
            case 0xB2:
                register.A = cpu8BitOR(register.A, register.D, false);
                return 4;
            case 0xB3:
                register.A = cpu8BitOR(register.A, register.E, false);
                return 4;
            case 0xB4:
                register.A = cpu8BitOR(register.A, register.H, false);
                return 4;
            case 0xB5:
                register.A = cpu8BitOR(register.A, register.L, false);
                return 4;
            case 0xB6:
                register.A = cpu8BitOR(register.A, memoryManager.readMemory(register.getHL()), false);
                return 8;
            case 0xF6:
                register.A = cpu8BitOR(register.A, 0, true);
                return 8;

            /* Logical XOR */
            case 0xAF:
                register.A = cpu8BitXOR(register.A, register.A, false);
                return 4;
            case 0xA8:
                register.A = cpu8BitXOR(register.A, register.getBC(), false);
                return 4;
            case 0xA9:
                register.A = cpu8BitXOR(register.A, register.C, false);
                return 4;
            case 0xAA:
                register.A = cpu8BitXOR(register.A, register.D, false);
                return 4;
            case 0xAB:
                register.A = cpu8BitXOR(register.A, register.E, false);
                return 4;
            case 0xAC:
                register.A = cpu8BitXOR(register.A, register.H, false);
                return 4;
            case 0xAD:
                register.A = cpu8BitXOR(register.A, register.L, false);
                return 4;
            case 0xAE:
                register.A = cpu8BitXOR(register.A, memoryManager.readMemory(register.getHL()), false);
                return 8;
            case 0xEE:
                register.A = cpu8BitXOR(register.A, 0, true);
                return 8;

            /* Compares */
            case 0xBF:
                cpuCompare(register.A, register.A, false);
                return 4;
            case 0xB8:
                cpuCompare(register.A, register.getBC(), false);
                return 4;
            case 0xB9:
                cpuCompare(register.A, register.C, false);
                return 4;
            case 0xBA:
                cpuCompare(register.A, register.D, false);
                return 4;
            case 0xBB:
                cpuCompare(register.A, register.E, false);
                return 4;
            case 0xBC:
                cpuCompare(register.A, register.H, false);
                return 4;
            case 0xBD:
                cpuCompare(register.A, register.L, false);
                return 4;
            case 0xBE:
                cpuCompare(register.A, memoryManager.readMemory(register.getHL()), false);
                return 8;
            case 0xFE:
                cpuCompare(register.A, 0, true);
                return 8;

            /* Increment */
            case 0x3C:
                register.A = cpu8BitInc(register.A);
                return 4;
            case 0x04:
                register.B = cpu8BitInc(register.B);
                return 4;
            case 0x0C:
                register.C = cpu8BitInc(register.C);
                return 4;
            case 0x14:
                register.D = cpu8BitInc(register.D);
                return 4;
            case 0x1C:
                register.E = cpu8BitInc(register.E);
                return 4;
            case 0x24:
                register.H = cpu8BitInc(register.H);
                return 4;
            case 0x2C:
                register.L = cpu8BitInc(register.L);
                return 4;
            case 0x34:
                cpuIncMemory(register.getHL());
                return 12;

            /* Decrement */
            case 0x3D:
                register.A = cpu8BitDec(register.A);
                return 4;
            case 0x05:
                register.B = cpu8BitDec(register.B);
                return 4;
            case 0x0D:
                register.C = cpu8BitDec(register.C);
                return 4;
            case 0x15:
                register.D = cpu8BitDec(register.D);
                return 4;
            case 0x1D:
                register.E = cpu8BitDec(register.E);
                return 4;
            case 0x25:
                register.H = cpu8BitDec(register.H);
                return 4;
            case 0x2D:
                register.L = cpu8BitDec(register.L);
                return 4;
            case 0x35:
                cpuDecMemory(register.getHL());
                return 12;

            /* 16-bit add */
            case 0x09:
                register.setHL(cpu16BitAdd(register.getHL(), register.getBC()));
                return 8;
            case 0x19:
                register.setHL( cpu16BitAdd(register.getHL(), register.getDE()));
                return 8;
            case 0x29:
                register.setHL( cpu16BitAdd(register.getHL(), register.getHL()));
                return 8;
            case 0x39:
                register.setHL( cpu16BitAdd(register.getHL(), register.sp));
                return 8;

            /* Add n to stack pointer */
            case 0xE8:
                cpu8BitSPAdd();
                return 16;

            /* 16 bit inc */
            case 0x03:
                register.setBC( cpu16BitInc(register.getBC()));
                return 8;
            case 0x13:
                register.setDE( cpu16BitInc(register.getDE()));
                return 8;
            case 0x23:
                register.setHL( cpu16BitInc(register.getHL()));
                return 8;
            case 0x33:
                register.sp = cpu16BitInc(register.sp);
                return 8;

            /* 16 bit DEC */
            case 0x0B:
                register.setBC( cpu16BitDec(register.getBC()));
                return 8;
            case 0x1B:
                register.setDE( cpu16BitDec(register.getDE()));
                return 8;
            case 0x2B:
                register.setHL( cpu16BitDec(register.getHL()));
                return 8;
            case 0x3B:
                register.sp = cpu16BitDec(register.sp);
                return 8;

            /* DAA */
            case 0x27:
                cpuDAA();
                return 4;

            /* Miscellaneous */
            case 0x2F:
                register.A = ~register.A;
                register.setN();
                register.setH();
                return 4;

            case 0x37:
                register.setC();
                register.clearN();
                register.clearH();
                return 4;

            case 0x76:
                this.isHalted = true;
                return 4;

            case 0x10: //Stop
                register.pc++;
                return 4;

            case 0xF3:
                this.interruptMaster = true;
                return 4;

            case 0xFB:
                this.pendingInterruptEnabled = true;
                return 4;

            /* Rotates and shifts */
            case 0x07:
                register.A = cpuRLC(register.A);
                return 4;

            case 0x17:
                register.A = cpuRL(register.A);
                return 4;

            case 0x0F:
                register.A = cpuRRC(register.A);
                return 4;

            case 0x1F:
                register.A = cpuRR(register.A);
                return 4;

            /* Jumps */
            case 0xC3:
                cpuJump(false, 0, false);
                return 16;
            case 0xC2:
                cpuJump(true, Register2.FLAG_Z, false);
                return 12;
            case 0xCA:
                cpuJump(true, Register2.FLAG_Z, true);
                return (register.isZ()) ? 16 : 12;
            case 0xD2:
                cpuJump(true, Register2.FLAG_C, false);
                return 12;
            case 0xDA:
                cpuJump(true, Register2.FLAG_C, true);
                return 12;
            case 0xE9:
                register.pc = register.getHL();
                return 4;
            case 0x18:
                cpuJumpImmediate(false, 0, false);
                return 12;
            case 0x20:
                cpuJumpImmediate(true, Register2.FLAG_Z, false);
                return register.isZ() ? 8 : 12;
            case 0x28:
                cpuJumpImmediate(true, Register2.FLAG_Z, true);
                return register.isZ() ? 12 : 8;
            case 0x30:
                cpuJumpImmediate(true, Register2.FLAG_C, false);
                return 8;
            case 0x38:
                cpuJumpImmediate(true, Register2.FLAG_C, true);
                return 8;

            /* Calls */
            case 0xCD:
                cpuCall(false, 0, false);
                return 24;
            case 0xC4:
                cpuCall(true, Register2.FLAG_Z, false);
                return 12;
            case 0xCC:
                cpuCall(true, Register2.FLAG_Z, true);
                return 12;
            case 0xD4:
                cpuCall(true, Register2.FLAG_C, false);
                return 12;
            case 0xDC:
                cpuCall(true, Register2.FLAG_C, true);
                return 12;

            /* returns */
            case 0xC9:
                cpuReturn(false, 0, false);
                return 16;
            case 0xC0:
                cpuReturn(true, Register2.FLAG_Z, false);
                return 8;
            case 0xC8:
                cpuReturn(true, Register2.FLAG_Z, true);
                return register.isZ() ? 20 : 8;
            case 0xD0:
                cpuReturn(true, Register2.FLAG_C, false);
                return 8;
            case 0xD8:
                cpuReturn(true, Register2.FLAG_C, true);
                return 8;

            /* Return from interrupt */
            case 0xD9:
                cpuReturnFromInterrupt();
                return 16;

            /* restarts */
            case 0xC7:
                cpuRestart(0x00);
                return 32;
            case 0xCF:
                cpuRestart(0x08);
                return 32;
            case 0xD7:
                cpuRestart(0x10);
                return 32;
            case 0xDF:
                cpuRestart(0x18);
                return 32;
            case 0xE7:
                cpuRestart(0x20);
                return 32;
            case 0xEF:
                cpuRestart(0x28);
                return 32;
            case 0xF7:
                cpuRestart(0x30);
                return 32;
            case 0xFF:
                cpuRestart(0x38);
                return 32;

            case 0xCB:
                   /* 0xCB itself takes 4 cycles to execute so add this to
                    * cycles taken by the extended opcode
                    */
                    return 4 + executeExtendedOpcode();
//                try {
//                   /* 0xCB itself takes 4 cycles to execute so add this to
//                    * cycles taken by the extended opcode
//                    */
//                    return 4 + executeExtendedOpcode();
//                }catch(Exception e) {
//                    throw e
//                }

            default:
                  String hexCode = java.lang.String.format("0x%2X", opcode);
                  System.out.println("Unrecognized opcode: " + hexCode);
                  System.exit(-1);
//                def hexCode = java.lang.String.format("0x%2X", opcode);
//                throw new Exception("Unrecognized opcode: " + hexCode);
        }
        return 0;
    }

    private int executeExtendedOpcode() {
        /*
         * When the opcode 0xCB is encountered the next immediate byte needs to be
         * decoded and treated as an opcode.
         */
        int opcode = memoryManager.readMemory(register.pc);
        register.pc++;

        switch (opcode) {
        /* Swaps */
        case 0x37:
            register.A = cpuSwapNibbles(register.A);
            return 8;
        case 0x30:
            register.B = cpuSwapNibbles(register.B);
            return 8;
        case 0x31:
            register.C = cpuSwapNibbles(register.C);
            return 8;
        case 0x32:
            register.D = cpuSwapNibbles(register.D);
            return 8;
        case 0x33:
            register.E = cpuSwapNibbles(register.E);
            return 8;
        case 0x34:
            register.H = cpuSwapNibbles(register.H);
            return 8;
        case 0x35:
            register.L = cpuSwapNibbles(register.L);
            return 8;

        /* Rotate left through carry */
        case 0x07:
            register.A = cpuRLC(register.A);
            return 8;
        case 0x00:
            register.B = cpuRLC(register.B);
            return 8;
        case 0x01:
            register.C = cpuRLC(register.C);
            return 8;
        case 0x02:
            register.D = cpuRLC(register.D);
            return 8;
        case 0x03:
            register.E = cpuRLC(register.E);
            return 8;
        case 0x04:
            register.H = cpuRLC(register.H);
            return 8;
        case 0x05:
            register.L = cpuRLC(register.L);
            return 8;
        case 0x06:
            cpuRLCMemory(register.getHL());
            return 16;

        /* rotate left */
        case 0x17:
            register.A = cpuRL(register.A);
            return 8;
        case 0x10:
            register.B = cpuRL(register.B);
            return 8;
        case 0x11:
            register.C = cpuRL(register.C);
            return 8;
        case 0x12:
            register.D = cpuRL(register.D);
            return 8;
        case 0x13:
            register.E = cpuRL(register.E);
            return 8;
        case 0x14:
            register.H = cpuRL(register.H);
            return 8;
        case 0x15:
            register.L = cpuRL(register.A);
            return 8;
        case 0x16:
            cpuRLMemory(register.getHL());
            return 16;

        /* Rotate right through carry */
        case 0x0F:
            register.A = cpuRRC(register.A);
            return 8;
        case 0x08:
            register.B = cpuRRC(register.B);
            return 8;
        case 0x09:
            register.C = cpuRRC(register.C);
            return 8;
        case 0x0A:
            register.D = cpuRRC(register.D);
            return 8;
        case 0x0B:
            register.E = cpuRRC(register.E);
            return 8;
        case 0x0C:
            register.H = cpuRRC(register.H);
            return 8;
        case 0x0D:
            register.L = cpuRRC(register.L);
            return 8;
        case 0x0E:
            cpuRRCMemory(register.getHL());
            return 16;

        /* rotate right */
        case 0x1F:
            register.A = cpuRR(register.A);
            return 8;
        case 0x18:
            register.B = cpuRR(register.B);
            return 8;
        case 0x19:
            register.C = cpuRR(register.C);
            return 8;
        case 0x1A:
            register.D = cpuRR(register.D);
            return 8;
        case 0x1B:
            register.E = cpuRR(register.E);
            return 8;
        case 0x1C:
            register.H = cpuRR(register.H);
            return 8;
        case 0x1D:
            register.L = cpuRR(register.L);
            return 8;
        case 0x1E:
            cpuRRMemory(register.getHL());
            return 16;

        /* shift left */
        case 0x27:
            register.A = cpuSLA(register.A);
            return 8;
        case 0x20:
            register.B = cpuSLA(register.B);
            return 8;
        case 0x21:
            register.C = cpuSLA(register.C);
            return 8;
        case 0x22:
            register.D = cpuSLA(register.D);
            return 8;
        case 0x23:
            register.E = cpuSLA(register.E);
            return 8;
        case 0x24:
            register.H = cpuSLA(register.H);
            return 8;
        case 0x25:
            register.L = cpuSLA(register.L);
            return 8;
        case 0x26:
            cpuSLAMemory(register.H);
            return 16;

        /* shift right into carry */
        case 0x2F:
            register.A = cpuSRA(register.A);
            return 8;
        case 0x28:
            register.B = cpuSRA(register.B);
            return 8;
        case 0x29:
            register.C = cpuSRA(register.C);
            return 8;
        case 0x2A:
            register.D = cpuSRA(register.D);
            return 8;
        case 0x2B:
            register.E = cpuSRA(register.E);
            return 8;
        case 0x2C:
            register.H = cpuSRA(register.H);
            return 8;
        case 0x2D:
            register.L = cpuSRA(register.L);
            return 8;
        case 0x2E:
            cpuSRAMemory(register.getHL());
            return 16;
        case 0x3F:
            register.A = cpuSRL(register.A);
            return 8;
        case 0x38:
            register.B = cpuSRL(register.B);
            return 8;
        case 0x39:
            register.C = cpuSRL(register.C);
            return 8;
        case 0x3A:
            register.D = cpuSRL(register.D);
            return 8;
        case 0x3B:
            register.E = cpuSRL(register.E);
            return 8;
        case 0x3C:
            register.H = cpuSRL(register.H);
            return 8;
        case 0x3D:
            register.L = cpuSRL(register.L);
            return 8;
        case 0x3E:
            cpuSRLMemory(register.getHL());
            return 16;

        /* Test Bit */
        case 0x40:
            cpuTestBit(register.getBC(), 0);
            return 8;
        case 0x41:
            cpuTestBit(register.C, 0);
            return 8;
        case 0x42:
            cpuTestBit(register.D, 0);
            return 8;
        case 0x43:
            cpuTestBit(register.E, 0);
            return 8;
        case 0x44:
            cpuTestBit(register.H, 0);
            return 8;
        case 0x45:
            cpuTestBit(register.L, 0);
            return 8;
        case 0x46:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 0);
            return 16;
        case 0x47:
            cpuTestBit(register.A, 0);
            return 8;
        case 0x48:
            cpuTestBit(register.getBC(), 1);
            return 8;
        case 0x49:
            cpuTestBit(register.C, 1);
            return 8;
        case 0x4A:
            cpuTestBit(register.D, 1);
            return 8;
        case 0x4B:
            cpuTestBit(register.E, 1);
            return 8;
        case 0x4C:
            cpuTestBit(register.H, 1);
            return 8;
        case 0x4D:
            cpuTestBit(register.L, 1);
            return 8;
        case 0x4E:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 1);
            return 16;
        case 0x4F:
            cpuTestBit(register.A, 1);
            return 8;
        case 0x50:
            cpuTestBit(register.getBC(), 2);
            return 8;
        case 0x51:
            cpuTestBit(register.C, 2);
            return 8;
        case 0x52:
            cpuTestBit(register.D, 2);
            return 8;
        case 0x53:
            cpuTestBit(register.E, 2);
            return 8;
        case 0x54:
            cpuTestBit(register.H, 2);
            return 8;
        case 0x55:
            cpuTestBit(register.L, 2);
            return 8;
        case 0x56:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 2);
            return 16;
        case 0x57:
            cpuTestBit(register.A, 2);
            return 8;
        case 0x58:
            cpuTestBit(register.getBC(), 3);
            return 8;
        case 0x59:
            cpuTestBit(register.C, 3);
            return 8;
        case 0x5A:
            cpuTestBit(register.D, 3);
            return 8;
        case 0x5B:
            cpuTestBit(register.E, 3);
            return 8;
        case 0x5C:
            cpuTestBit(register.H, 3);
            return 8;
        case 0x5D:
            cpuTestBit(register.L, 3);
            return 8;
        case 0x5E:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 3);
            return 16;
        case 0x5F:
            cpuTestBit(register.A, 3);
            return 8;
        case 0x60:
            cpuTestBit(register.getBC(), 4);
            return 8;
        case 0x61:
            cpuTestBit(register.C, 4);
            return 8;
        case 0x62:
            cpuTestBit(register.D, 4);
            return 8;
        case 0x63:
            cpuTestBit(register.E, 4);
            return 8;
        case 0x64:
            cpuTestBit(register.H, 4);
            return 8;
        case 0x65:
            cpuTestBit(register.L, 4);
            return 8;
        case 0x66:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 4);
            return 16;
        case 0x67:
            cpuTestBit(register.A, 4);
            return 8;
        case 0x68:
            cpuTestBit(register.getBC(), 5);
            return 8;
        case 0x69:
            cpuTestBit(register.C, 5);
            return 8;
        case 0x6A:
            cpuTestBit(register.D, 5);
            return 8;
        case 0x6B:
            cpuTestBit(register.E, 5);
            return 8;
        case 0x6C:
            cpuTestBit(register.H, 5);
            return 8;
        case 0x6D:
            cpuTestBit(register.L, 5);
            return 8;
        case 0x6E:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 5);
            return 16;
        case 0x6F:
            cpuTestBit(register.A, 5);
            return 8;
        case 0x70:
            cpuTestBit(register.getBC(), 6);
            return 8;
        case 0x71:
            cpuTestBit(register.C, 6);
            return 8;
        case 0x72:
            cpuTestBit(register.D, 6);
            return 8;
        case 0x73:
            cpuTestBit(register.E, 6);
            return 8;
        case 0x74:
            cpuTestBit(register.H, 6);
            return 8;
        case 0x75:
            cpuTestBit(register.L, 6);
            return 8;
        case 0x76:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 6);
            return 16;
        case 0x77:
            cpuTestBit(register.A, 6);
            return 8;
        case 0x78:
            cpuTestBit(register.getBC(), 7);
            return 8;
        case 0x79:
            cpuTestBit(register.C, 7);
            return 8;
        case 0x7A:
            cpuTestBit(register.D, 7);
            return 8;
        case 0x7B:
            cpuTestBit(register.E, 7);
            return 8;
        case 0x7C:
            cpuTestBit(register.H, 7);
            return 8;
        case 0x7D:
            cpuTestBit(register.L, 7);
            return 8;
        case 0x7E:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 7);
            return 16;
        case 0x7F:
            cpuTestBit(register.A, 7);
            return 8;

        /* set bit */
        case 0xC0:
            register.B = cpuSetBit(register.getBC(), 0);
            return 8;
        case 0xC1:
            register.C = cpuSetBit(register.C, 0);
            return 8;
        case 0xC2:
            register.D = cpuSetBit(register.D, 0);
            return 8;
        case 0xC3:
            register.E = cpuSetBit(register.E, 0);
            return 8;
        case 0xC4:
            register.H = cpuSetBit(register.H, 0);
            return 8;
        case 0xC5:
            register.L = cpuSetBit(register.L, 0);
            return 8;
        case 0xC6:
            cpuSetBitMemory(register.getHL(), 0);
            return 16;
        case 0xC7:
            register.A = cpuSetBit(register.A, 0);
            return 8;
        case 0xC8:
            register.B = cpuSetBit(register.getBC(), 1);
            return 8;
        case 0xC9:
            register.C = cpuSetBit(register.C, 1);
            return 8;
        case 0xCA:
            register.D = cpuSetBit(register.D, 1);
            return 8;
        case 0xCB:
            register.E = cpuSetBit(register.E, 1);
            return 8;
        case 0xCC:
            register.H = cpuSetBit(register.H, 1);
            return 8;
        case 0xCD:
            register.L = cpuSetBit(register.L, 1);
            return 8;
        case 0xCE:
            cpuSetBitMemory(register.getHL(), 1);
            return 16;
        case 0xCF:
            register.A = cpuSetBit(register.A, 1);
            return 8;
        case 0xD0:
            register.B = cpuSetBit(register.getBC(), 2);
            return 8;
        case 0xD1:
            register.C = cpuSetBit(register.C, 2);
            return 8;
        case 0xD2:
            register.D = cpuSetBit(register.D, 2);
            return 8;
        case 0xD3:
            register.E = cpuSetBit(register.E, 2);
            return 8;
        case 0xD4:
            register.H = cpuSetBit(register.H, 2);
            return 8;
        case 0xD5:
            register.L = cpuSetBit(register.L, 2);
            return 8;
        case 0xD6:
            cpuSetBitMemory(register.getHL(), 2);
            return 16;
        case 0xD7:
            register.A = cpuSetBit(register.A, 2);
            return 8;
        case 0xD8:
            register.B = cpuSetBit(register.getBC(), 3);
            return 8;
        case 0xD9:
            register.C = cpuSetBit(register.C, 3);
            return 8;
        case 0xDA:
            register.D = cpuSetBit(register.D, 3);
            return 8;
        case 0xDB:
            register.E = cpuSetBit(register.E, 3);
            return 8;
        case 0xDC:
            register.H = cpuSetBit(register.H, 3);
            return 8;
        case 0xDD:
            register.L = cpuSetBit(register.L, 3);
            return 8;
        case 0xDE:
            cpuSetBitMemory(register.getHL(), 3);
            return 16;
        case 0xDF:
            register.A = cpuSetBit(register.A, 3);
            return 8;
        case 0xE0:
            register.B = cpuSetBit(register.getBC(), 4);
            return 8;
        case 0xE1:
            register.C = cpuSetBit(register.C, 4);
            return 8;
        case 0xE2:
            register.D = cpuSetBit(register.D, 4);
            return 8;
        case 0xE3:
            register.E = cpuSetBit(register.E, 4);
            return 8;
        case 0xE4:
            register.H = cpuSetBit(register.H, 4);
            return 8;
        case 0xE5:
            register.L = cpuSetBit(register.L, 4);
            return 8;
        case 0xE6:
            cpuSetBitMemory(register.getHL(), 4);
            return 16;
        case 0xE7:
            register.A = cpuSetBit(register.A, 4);
            return 8;
        case 0xE8:
            register.B = cpuSetBit(register.getBC(), 5);
            return 8;
        case 0xE9:
            register.C = cpuSetBit(register.C, 5);
            return 8;
        case 0xEA:
            register.D = cpuSetBit(register.D, 5);
            return 8;
        case 0xEB:
            register.E = cpuSetBit(register.E, 5);
            return 8;
        case 0xEC:
            register.H = cpuSetBit(register.H, 5);
            return 8;
        case 0xED:
            register.L = cpuSetBit(register.L, 5);
            return 8;
        case 0xEE:
            cpuSetBitMemory(register.getHL(), 5);
            return 16;
        case 0xEF:
            register.A = cpuSetBit(register.A, 5);
            return 8;
        case 0xF0:
            register.B = cpuSetBit(register.getBC(), 6);
            return 8;
        case 0xF1:
            register.C = cpuSetBit(register.getBC(), 6);
            return 8;
        case 0xF2:
            register.D = cpuSetBit(register.D, 6);
            return 8;
        case 0xF3:
            register.E = cpuSetBit(register.E, 6);
            return 8;
        case 0xF4:
            register.H = cpuSetBit(register.H, 6);
            return 8;
        case 0xF5:
            register.L = cpuSetBit(register.L, 6);
            return 8;
        case 0xF6:
            cpuSetBitMemory(register.getHL(), 6);
            return 16;
        case 0xF7:
            register.A = cpuSetBit(register.A, 6);
            return 8;
        case 0xF8:
            register.B = cpuSetBit(register.getBC(), 7);
            return 8;
        case 0xF9:
            register.C = cpuSetBit(register.C, 7);
            return 8;
        case 0xFA:
            register.D = cpuSetBit(register.D, 7);
            return 8;
        case 0xFB:
            register.E = cpuSetBit(register.E, 7);
            return 8;
        case 0xFC:
            register.H = cpuSetBit(register.H, 7);
            return 8;
        case 0xFD:
            register.L = cpuSetBit(register.L, 7);
            return 8;
        case 0xFE:
            cpuSetBitMemory(register.getHL(), 7);
            return 16;
        case 0xFF:
            register.A = cpuSetBit(register.A, 7);
            return 8;

        /* reset bit */
        case 0x80:
            register.B = cpuResetBit(register.getBC(), 0);
            return 8;
        case 0x81:
            register.C = cpuResetBit(register.C, 0);
            return 8;
        case 0x82:
            register.D = cpuResetBit(register.D, 0);
            return 8;
        case 0x83:
            register.E = cpuResetBit(register.E, 0);
            return 8;
        case 0x84:
            register.H = cpuResetBit(register.H, 0);
            return 8;
        case 0x85:
            register.L = cpuResetBit(register.L, 0);
            return 8;
        case 0x86:
            cpuResetBitMemory(register.getHL(), 0);
            return 16;
        case 0x87:
            register.A = cpuResetBit(register.A, 0);
            return 8;
        case 0x88:
            register.B = cpuResetBit(register.getBC(), 1);
            return 8;
        case 0x89:
            register.C = cpuResetBit(register.C, 1);
            return 8;
        case 0x8A:
            register.D = cpuResetBit(register.D, 1);
            return 8;
        case 0x8B:
            register.E = cpuResetBit(register.E, 1);
            return 8;
        case 0x8C:
            register.H = cpuResetBit(register.H, 1);
            return 8;
        case 0x8D:
            register.L = cpuResetBit(register.L, 1);
            return 8;
        case 0x8E:
            cpuResetBitMemory(register.getHL(), 1);
            return 16;
        case 0x8F:
            register.A = cpuResetBit(register.A, 1);
            return 8;
        case 0x90:
            register.B = cpuResetBit(register.getBC(), 2);
            return 8;
        case 0x91:
            register.C = cpuResetBit(register.C, 2);
            return 8;
        case 0x92:
            register.D = cpuResetBit(register.D, 2);
            return 8;
        case 0x93:
            register.E = cpuResetBit(register.E, 2);
            return 8;
        case 0x94:
            register.H = cpuResetBit(register.H, 2);
            return 8;
        case 0x95:
            register.L = cpuResetBit(register.L, 2);
            return 8;
        case 0x96:
            cpuResetBitMemory(register.getHL(), 2);
            return 16;
        case 0x97:
            register.A = cpuResetBit(register.A, 2);
            return 8;
        case 0x98:
            register.B = cpuResetBit(register.getBC(), 3);
            return 8;
        case 0x99:
            register.C = cpuResetBit(register.C, 3);
            return 8;
        case 0x9A:
            register.D = cpuResetBit(register.D, 3);
            return 8;
        case 0x9B:
            register.E = cpuResetBit(register.E, 3);
            return 8;
        case 0x9C:
            register.H = cpuResetBit(register.H, 3);
            return 8;
        case 0x9D:
            register.L = cpuResetBit(register.L, 3);
            return 8;
        case 0x9E:
            cpuResetBitMemory(register.getHL(), 3);
            return 16;
        case 0x9F:
            register.A = cpuResetBit(register.A, 3);
            return 8;
        case 0xA0:
            register.B = cpuResetBit(register.getBC(), 4);
            return 8;
        case 0xA1:
            register.C = cpuResetBit(register.C, 4);
            return 8;
        case 0xA2:
            register.D = cpuResetBit(register.D, 4);
            return 8;
        case 0xA3:
            register.E = cpuResetBit(register.E, 4);
            return 8;
        case 0xA4:
            register.H = cpuResetBit(register.H, 4);
            return 8;
        case 0xA5:
            register.L = cpuResetBit(register.L, 4);
            return 8;
        case 0xA6:
            cpuResetBitMemory(register.getHL(), 4);
            return 16;
        case 0xA7:
            register.A = cpuResetBit(register.A, 4);
            return 8;
        case 0xA8:
            register.B = cpuResetBit(register.getBC(), 5);
            return 8;
        case 0xA9:
            register.C = cpuResetBit(register.C, 5);
            return 8;
        case 0xAA:
            register.D = cpuResetBit(register.D, 5);
            return 8;
        case 0xAB:
            register.E = cpuResetBit(register.E, 5);
            return 8;
        case 0xAC:
            register.H = cpuResetBit(register.H, 5);
            return 8;
        case 0xAD:
            register.L = cpuResetBit(register.L, 5);
            return 8;
        case 0xAE:
            cpuResetBitMemory(register.getHL(), 5);
            return 16;
        case 0xAF:
            register.A = cpuResetBit(register.A, 5);
            return 8;
        case 0xB0:
            register.B = cpuResetBit(register.getBC(), 6);
            return 8;
        case 0xB1:
            register.C = cpuResetBit(register.C, 6);
            return 8;
        case 0xB2:
            register.D = cpuResetBit(register.D, 6);
            return 8;
        case 0xB3:
            register.E = cpuResetBit(register.E, 6);
            return 8;
        case 0xB4:
            register.H = cpuResetBit(register.H, 6);
            return 8;
        case 0xB5:
            register.L = cpuResetBit(register.L, 6);
            return 8;
        case 0xB6:
            cpuResetBitMemory(register.getHL(), 6);
            return 16;
        case 0xB7:
            register.A = cpuResetBit(register.A, 6);
            return 8;
        case 0xB8:
            register.B = cpuResetBit(register.getBC(), 7);
            return 8;
        case 0xB9:
            register.C = cpuResetBit(register.C, 7);
            return 8;
        case 0xBA:
            register.D = cpuResetBit(register.D, 7);
            return 8;
        case 0xBB:
            register.E = cpuResetBit(register.E, 7);
            return 8;
        case 0xBC:
            register.H = cpuResetBit(register.H, 7);
            return 8;
        case 0xBD:
            register.L = cpuResetBit(register.L, 7);
            return 8;
        case 0xBE:
            cpuResetBitMemory(register.getHL(), 7);
            return 16;
        case 0xBF:
            register.A = cpuResetBit(register.A, 7);
            return 8;
        default:
            String hexCode = java.lang.String.format("0x%2X", opcode);
            System.out.println("Unrecognized extended opcode: " + hexCode);
//                def hexCode = java.lang.String.format("0x%2X", opcode);
//                throw new Exception("Unrecognized extended opcode: " + hexCode);
        }
        return 0;
    }

    // STOLEN! :-);
    private void cpuDAA() {
        if (!register.isN()) {
            if (register.isC() || register.A > 0x99) {
                register.A = register.A + 0x60;
                register.setC();
            }

            if (register.isH() || ((register.A & 0x0F) > 0x09)) {
                register.A = register.A + 0x6;
            } else {
                if (register.isC()) {
                    register.A = register.A - 0x60;
                }
                if (register.isH()) {
                    register.A = register.A - 0x6;
                }
            }
        }

        register.setZ(register.A == 0);
        register.clearH();
    }

    private void cpuRestart(int n) {
        memoryManager.push(register.pc);
        register.pc = n;
    }

    private void cpuCall(boolean useCondition, int flag, boolean condition) {
        int word = memoryManager.readWord();
        /*
         * Advance 2 positions ahead because two bytes were just read.
         */
        register.pc = register.pc + 2;

        if (!useCondition) {
            memoryManager.push(register.pc);
            register.pc = word;
            return;
        }

        if (BitUtil.isSet(register.F, flag) == condition) {
            memoryManager.push(register.pc);
            register.pc = word;
        }
    }

    private void cpuReturn(boolean useCondition, int flag, boolean condition) {
        if (!useCondition) {
            register.pc = memoryManager.pop();
            return;
        }

        if (BitUtil.isSet(register.F, flag) == condition) {
            register.pc = memoryManager.pop();
        }
    }

    private void cpuReturnFromInterrupt() {
        register.pc = memoryManager.pop();
        interruptMaster = true;
    }

    private void cpuJump(boolean useJumpCondition, int flag, boolean jumpCondition) {
        int nn = memoryManager.readWord();
        register.pc = register.pc + 2;

        if (!useJumpCondition) {
            register.pc = nn;
            return;
        }

        if (BitUtil.isSet(register.F, flag) == jumpCondition) {
            register.pc = nn;
        }
    }

    public void cpuJumpImmediate(boolean useCondition, int flag, boolean condition) {
        byte n = (byte) memoryManager.readMemory(register.pc);

        if (!useCondition) {
            /*
             * Jump unconditionally
             */
            register.pc = register.pc + n;
        } else if (BitUtil.isSet(register.F, flag) == condition) {
            /*
             * Only jump if the condition is met
             */
            register.pc = register.pc + n;
        }
        register.pc++;
    }

    private void cpuTestBit(int reg, int bit) {
        /*
         * This tests the bit of a byte and sets the following flags:
         *
         * FLAG_Z : set to 1 if the bit is 0 FLAG_N : Set to 0 FLAG_C : Unchanged FLAG_H
         * : Set to 1
         */

        register.setZ(BitUtil.isSet(reg, bit));

        register.clearN();
        register.setH();
    }

    private int cpuResetBit(int reg, int bit) {
        return BitUtil.clearBit(reg, bit);
    }

    private void cpuResetBitMemory(int address, int bit) {
        int memory = memoryManager.readMemory(address);
        memory = BitUtil.clearBit(memory, bit);
        memoryManager.writeMemory(address, memory);
    }

    private int cpuSetBit(int reg, int bit) {
        return BitUtil.setBit(reg, bit);
    }

    private void cpuSetBitMemory(int address, int bit) {
        int memory = memoryManager.readMemory(address);
        memory = BitUtil.setBit(memory, bit);
        memoryManager.writeMemory(address, memory);
    }

    private int cpuSRL(int reg) {
        boolean isLSBSet = BitUtil.isSet(reg, 0);

        reg = (reg >> 1) & 0xFF;

        register.clearN();
        register.clearH();
        register.setC(isLSBSet);
        register.setZ(reg == 0);

        return reg;
    }

    private void cpuSRLMemory(int address) {
        int reg = memoryManager.readMemory(address);
        boolean isLSBSet = BitUtil.isSet(reg, 0);

        reg = (reg >> 1) & 0xFF;

        register.clearN();
        register.clearH();
        register.setC(isLSBSet);
        register.setZ(reg == 0);

        memoryManager.writeMemory(address, reg);
    }

    private int cpuSRA(int reg) {
        boolean isLSBSet = BitUtil.isSet(reg, 0);
        boolean isMSBSet = BitUtil.isSet(reg, 7);

        reg = (reg >> 1) & 0xFF;

        if (isMSBSet) {
            reg = BitUtil.setBit(reg, 7);
        }

        register.clearN();
        register.clearH();
        register.setC(isLSBSet);
        register.setZ(reg == 0);

        return reg;
    }

    private void cpuSRAMemory(int address) {
        int reg = memoryManager.readMemory(address);

        boolean isLSBSet = BitUtil.isSet(reg, 0);
        boolean isMSBSet = BitUtil.isSet(reg, 7);

        reg = (reg >> 1) & 0xFF;

        if (isMSBSet) {
            reg = BitUtil.setBit(reg, 7);
        }

        register.clearN();
        register.clearH();
        register.setC(isLSBSet);
        register.setZ(reg == 0);

        memoryManager.writeMemory(address, reg);
    }

    private int cpuSLA(int reg) {
        register.setC(BitUtil.isSet(reg, 7));
        reg = (reg << 1) & 0xFF;
        register.clearN();
        register.clearH();
        register.setZ(reg == 0);
        return reg;
    }

    private void cpuSLAMemory(int address) {
        int reg = memoryManager.readMemory(address);
        reg = (reg << 1) & 0xFF;
        register.clearN();
        register.clearH();
        register.setC(BitUtil.isSet(reg, 7));
        register.setZ(reg == 0);

        memoryManager.writeMemory(address, reg);
    }

    private int cpuRLC(int reg) {
        int n = reg & 0xFF;
        register.A = (reg << 1 | reg >> 7) & 0xFF;
        register.setZ(reg == 0);
        register.clearN();
        register.clearH();
        register.setC(BitUtil.isSet(n, 7));

        return reg;
    }

    private void cpuRLCMemory(int address) {
        int reg = memoryManager.readMemory(address);

        boolean isMSBSet = BitUtil.isSet(reg, 7);

        reg = (reg << 1) & 0xFF;

        register.clearN();
        register.clearH();

        if (isMSBSet) {
            register.setC();
            reg = BitUtil.setBit(reg, 0);
        }

        register.setZ(reg == 0);
        memoryManager.writeMemory(address, reg);
    }

    private int cpuRL(int reg) {
        boolean carrySet = register.isC();
        boolean isMSBSet = BitUtil.isSet(reg, 7);

        reg = (reg << 1) & 0xFF;

        register.clearH();
        register.clearN();
        register.setC(isMSBSet);

        if (carrySet) {
            reg = BitUtil.setBit(reg, 0);
        }

        register.setZ(reg == 0);

        return reg;
    }

    private void cpuRLMemory(int address) {
        int reg = memoryManager.readMemory(address);

        boolean isCarrySet = register.isC();
        boolean isMSBSet = BitUtil.isSet(reg, 7);

        reg = (reg << 1) & 0xFF;

        register.clearH();
        register.clearN();
        register.setC(isMSBSet);

        if (isCarrySet) {
            reg = BitUtil.setBit(reg, 0);
        }

        register.setZ(reg == 0);
        memoryManager.writeMemory(address, reg);
    }

    private int cpuRRC(int reg) {
        boolean isLSBSet = BitUtil.isSet(reg, 0);

        register.clearAllFlags();

        reg = (reg >> 1) & 0xFF;

        if (isLSBSet) {
            register.setC();
            reg = BitUtil.setBit(reg, 7);
        }

        if (reg == 0) {
            register.setZ();
        }

        return reg;
    }

    private void cpuRRCMemory(int address) {
        int reg = memoryManager.readMemory(address);

        boolean isLSBSet = BitUtil.isSet(reg, 0);

        reg = (reg >> 1) & 0xFF;

        register.clearN();
        register.clearH();

        if (isLSBSet) {
            register.setC();
            reg = BitUtil.setBit(reg, 7);
        }

        register.setZ(reg == 0);
        memoryManager.writeMemory(address, reg);
    }

    private int cpuRR(int reg) {
        boolean carrySet = register.isC();
        boolean isLSBSet = BitUtil.isSet(reg, 0);

        reg = (reg >> 1) & 0xFF;

        register.clearH();
        register.clearN();
        register.setC(isLSBSet);

        if (carrySet) {
            reg = BitUtil.setBit(reg, 7);
        }

        register.setZ(reg == 0);

        return reg;
    }

    private void cpuRRMemory(int address) {
        int reg = memoryManager.readMemory(address);

        boolean isCarrySet = register.isC();
        boolean isLSBSet = BitUtil.isSet(reg, 0);

        reg = (reg >> 1) & 0xFF;

        register.clearN();
        register.clearH();
        register.setC(isLSBSet);

        if (isCarrySet) {
            reg = BitUtil.setBit(reg, 7);
        }

        register.setZ(reg == 0);
        memoryManager.writeMemory(address, reg);
    }

    private int cpuSwapNibbles(int n) {
        n = (((n & 0xF0) >> 4) | ((n & 0x0F) << 4));

        register.clearAllFlags();
        register.setZ(n == 0);
        return n;
    }

    private int cpu16BitImmediateLoad() {
        int nn = memoryManager.readWord();
        register.pc += 2;
        return nn;
    }

    private void cpuCompare(int A, int val, boolean useImmediate) {
        int n = 0;

        if (useImmediate) {
            n = memoryManager.readMemory(register.pc);
            register.pc++;
        } else {
            n = val;
        }

        register.setZ(A == n);
        register.setC(A < n);
        register.setH((n & 0x0F) > (A & 0x0F));
        register.setN(true);
    }

    private int cpu8BitInc(int n) {
        int initialN = n;
        n++;
        register.setZ(n == 0);
        register.setN(false);
        register.setH(((initialN & 0x0F) + (1 & 0x0F)) > 0x0F);
        return n;
    }

    private void cpuIncMemory(int address) {
        int n = memoryManager.readMemory(address);
        int initialN = n;
        n++;
        memoryManager.writeMemory(address, n);

        register.setZ(n == 0);
        register.setN(false);
        register.setH(((initialN & 0x0F) + (1 & 0x0F)) > 0x0F);
    }

    private int cpu8BitDec(int n) {
        int initialN = n;
        n--;
        register.setZ(n == 0);
        register.setN(true);
        register.setH((initialN & 0x0F) == 0);
        return n;
    }

    private void cpuDecMemory(int address) {
        int n = memoryManager.readMemory(address);
        int initialN = n;
        n--;
        memoryManager.writeMemory(address, n);

        register.setZ(n == 0);
        register.setN(true);
        register.setH((initialN & 0x0F) == 0);
    }

    private void cpu16BitLDHL() {
        /* If problems occur, double check the implementation of this method */
        int n = memoryManager.readMemory(register.sp);
        register.pc++;
        int result = register.sp + n;

        register.setHL(result & 0xFFFF);

        register.clearZ();
        register.clearN();

        if (result > 0xFFFF) {
            register.setC();
        } else {
            register.clearC();
        }

        if ((register.sp & 0xF) + (n & 0xF) > 0xF) {
            register.setH();
        } else {
            register.clearH();
        }
    }

    private int cpu8BitAND(int reg, int value, boolean useImmediate) {
        int n = 0;

        if (useImmediate) {
            n = memoryManager.readMemory(register.pc);
            register.pc++;
        } else {
            n = value;
        }

        reg = reg & n;

        register.clearN();
        register.setH();
        register.clearC();
        register.setZ(reg == 0x0);

        return reg;
    }

    private int cpu8BitOR(int reg, int value, boolean useImmediate) {
        int n = 0;

        if (useImmediate) {
            n = memoryManager.readMemory(register.pc);
            register.pc++;
        } else {
            n = value;
        }

        reg = reg | n;

        register.clearN();
        register.clearH();
        register.clearC();

        register.setZ(reg == 0);

        return reg;
    }

    private int cpu8BitLoad() {
        int n = memoryManager.readMemory(register.pc);
        register.pc++;
        return n;
    }

    private int cpuLoadImmediate16BitMemory() {
        int nn = memoryManager.readWord();
        register.pc += 2; /* Memory is stored in bytes and 1 word (2 bytes) are read */
        int n = memoryManager.readMemory(nn);
        return n;
    }

    private void cpuLoadRegisterToImmediateByte(int reg) {
        int nn = memoryManager.readWord();
        register.pc += 2;
        memoryManager.writeMemory(nn, reg);
    }

    private int cpuRegisterLoad(int register2) {
        return register2;
    }

    private void cpuLoadImmediate8BitMemory(int destination) {
        int data = memoryManager.readMemory(register.pc);
        register.pc++;
        memoryManager.writeMemory(destination, data);
    }

    private void cpuLoadRegisterToMemory(int address, int data) {
        memoryManager.writeMemory(address, data);
    }

    private int cpuROMLoad(int address) {
        return memoryManager.readMemory(address);
    }

    private int cpu16BitDec(int reg) {
        return (reg - 1);
    }

    private int cpu16BitInc(int reg) {
        return (reg + 1);
    }

    private int cpu8BitAdd(int reg, int value, boolean addImmediate, boolean addCarry) {
        int initialValue = reg;
        int runningSum = 0;

        if (addImmediate) {
            int n = memoryManager.readMemory(register.pc);
            register.pc++;
            runningSum = n;
        } else {
            runningSum = value;
        }

        if (addCarry) {
            if (register.isC()) {
                runningSum++;
            }
        }

        reg = reg + runningSum;

        /* Set flags */
        register.clearAllFlags();

        register.setZ(reg == 0);

        int halfCarry = initialValue & 0xF;
        halfCarry = halfCarry + (runningSum & 0xF);

        // if(halfCarry > 0xF) {
        // register.setH();
        // }
        //
        // if((initialValue + runningSum) > 0xFF) {
        // register.setC();
        // }
        register.setH(halfCarry > 0xF);
        register.setC((initialValue + runningSum) > 0xFF);

        return reg;
    }

    private int cpu16BitAdd(int reg, int n) {
        int initialHL = reg;
        reg = reg + n;

        register.clearN();

        /*
         * The half-carry is set if an overflow occurs from bit position 11. If the
         * initial value of the first 12 bits are greater than the 12 bits of the
         * result, it means that the result overflowed the bit at position 11 and is
         * therefore a greater value, i.e, a half-carry occurred. Otherwise, the value
         * of the first 12 bits will be greater than the value of the initial 12 bits
         * meaning that no overflow occurred.
         */
        register.setH((initialHL & 0xFFF) > (reg & 0xFFF));
        register.setC(reg > 0xFFFF);

        return reg;
    }

    private int cpu8BitSub(int reg, int value, boolean useImmediate, boolean subCarry) {
        int initialValue = reg;
        int runningDifference = 0;

        if (useImmediate) {
            int n = memoryManager.readMemory(register.pc);
            register.pc++;
            runningDifference = n;
        } else {
            runningDifference = value;
        }

        if (subCarry) {
            if (register.isC()) {
                runningDifference++;
            }
        }

        reg = reg - runningDifference;

        /* now set flags */
        register.clearAllFlags();

        register.setZ(reg == 0);

        register.setN();

        if (initialValue < runningDifference) {
            register.setC();
        }

        int halfCarry = initialValue & 0xF;
        halfCarry = halfCarry - (runningDifference & 0xF);

        if (halfCarry < 0) {
            register.setH();
        }

        return reg;
    }

    private void cpu8BitSPAdd() {
        int n = memoryManager.readMemory(register.pc);
        int result = (register.pc + n) & 0xFFFF;
        register.pc = result;

        register.clearZ();
        register.clearN();

        /*
         * To understand the logic behind setting the carry and half carry flag, see the
         * following stackoverflow questions.
         *
         * https://stackoverflow.com/questions/62006764/how-is-xor-applied-when-
         * determining-carry
         * https://stackoverflow.com/questions/20494087/is-it-possible-to-write-a-
         * function-adding-two-integers-without-control-flow-and/20500295#20500295
         * https://stackoverflow.com/questions/9070937/adding-two-numbers-without-
         * operator-clarification
         */

        register.setC(((register.pc ^ n ^ result) & 0x100) != 0);
        register.setH(((register.pc ^ n ^ result) & 0x10) != 0);
    }

    private int cpu8BitXOR(int reg, int value, boolean useImmediate) {

        int n = 0;

        if (useImmediate) {
            n = memoryManager.readMemory(register.pc);
            register.pc++;
        } else {
            n = value;
        }

        reg = reg ^ n;

        register.clearAllFlags();

        register.setZ(reg == 0);

        return reg;
    }
}
