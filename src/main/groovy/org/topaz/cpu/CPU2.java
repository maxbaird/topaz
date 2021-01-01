package org.topaz.cpu;

import org.topaz.MemoryManager;
import org.topaz.Topaz;
import org.topaz.util.BitUtil;
import org.topaz.util.UInt;
import org.topaz.debug.StateDumper;
import org.topaz.debug.Debug;

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
        int opcode = memoryManager.readMemory(register.pc.getValue());
        
        String hexCode = java.lang.String.format("0x%02X", opcode);
        boolean display = (n >= Topaz.executionStart && n <= Topaz.executionEnd) ? true : false;

        register.pc.inc();
        Debug.print("Register.pc: " + register.pc.getValue(), 174983, false);
        int extendedOpcode = (opcode == 0xCB) ? memoryManager.readMemory(register.pc.getValue()) : 0x0;
        // : 0x0

        cycles = executeOpcode(opcode);

        if(display) {
            String exOpcode = String.format("0x%02X", extendedOpcode);
            dumper.dump(n, hexCode, exOpcode, cycles, "/tmp/" + n + ".topaz");
        }
        Debug.print("Register.pc: " + register.pc.getValue(), 174983, false);
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
                register.B.setValue(cpu8BitLoad());
                return 8;
            case 0x0E:
                register.C.setValue(cpu8BitLoad());
                return 8;
            case 0x16:
                register.D.setValue(cpu8BitLoad());
                return 8;
            case 0x1E:
                register.E.setValue(cpu8BitLoad());
                return 8;
            case 0x26:
                register.H.setValue(cpu8BitLoad());
                return 8;
            case 0x2E:
                register.L.setValue(cpu8BitLoad());
                return 8;

            /* Register Loads */
            case 0x7F:
                register.A.setValue(cpuRegisterLoad(register.A));
                return 4;
            case 0x78:
                register.A.setValue(cpuRegisterLoad(register.B));
                return 4;
            case 0x79:
                register.A.setValue(cpuRegisterLoad(register.C));
                return 4;
            case 0x7A:
                register.A.setValue(cpuRegisterLoad(register.D));
                return 4;
            case 0x7B:
                register.A.setValue(cpuRegisterLoad(register.E));
                return 4;
            case 0x7C:
                register.A.setValue(cpuRegisterLoad(register.H));
                return 4;
            case 0x7D:
                register.A.setValue(cpuRegisterLoad(register.L));
                return 4;
            case 0x40:
                register.B.setValue(cpuRegisterLoad(register.B));
                return 4;
            case 0x41:
                register.B.setValue(cpuRegisterLoad(register.C));
                return 4;
            case 0x42:
                register.B.setValue(cpuRegisterLoad(register.D));
                return 4;
            case 0x43:
                register.B.setValue(cpuRegisterLoad(register.E));
                return 4;
            case 0x44:
                register.B.setValue(cpuRegisterLoad(register.H));
                return 4;
            case 0x45:
                register.B.setValue(cpuRegisterLoad(register.L));
                return 4;
            case 0x48:
                register.C.setValue(cpuRegisterLoad(register.B));
                return 4;
            case 0x49:
                register.C.setValue(cpuRegisterLoad(register.C));
                return 4;
            case 0x4A:
                register.C.setValue(cpuRegisterLoad(register.D));
                return 4;
            case 0x4B:
                register.C.setValue(cpuRegisterLoad(register.E));
                return 4;
            case 0x4C:
                register.C.setValue(cpuRegisterLoad(register.H));
                return 4;
            case 0x4D:
                register.C.setValue(cpuRegisterLoad(register.L));
                return 4;
            case 0x50:
                register.D.setValue(cpuRegisterLoad(register.B));
                return 4;
            case 0x51:
                register.D.setValue(cpuRegisterLoad(register.C));
                return 4;
            case 0x52:
                register.D.setValue(cpuRegisterLoad(register.D));
                return 4;
            case 0x53:
                register.D.setValue(cpuRegisterLoad(register.E));
                return 4;
            case 0x54:
                register.D.setValue(cpuRegisterLoad(register.H));
                return 4;
            case 0x55:
                register.D.setValue(cpuRegisterLoad(register.L));
                return 4;
            case 0x58:
                register.E.setValue(cpuRegisterLoad(register.B));
                return 4;
            case 0x59:
                register.E.setValue(cpuRegisterLoad(register.C));
                return 4;
            case 0x5A:
                register.E.setValue(cpuRegisterLoad(register.D));
                return 4;
            case 0x5B:
                register.E.setValue(cpuRegisterLoad(register.E));
                return 4;
            case 0x5C:
                register.E.setValue(cpuRegisterLoad(register.H));
                return 4;
            case 0x5D:
                register.E.setValue(cpuRegisterLoad(register.L));
                return 4;
            case 0x60:
                register.H.setValue(cpuRegisterLoad(register.B));
                return 4;
            case 0x61:
                register.H.setValue(cpuRegisterLoad(register.C));
                return 4;
            case 0x62:
                register.H.setValue(cpuRegisterLoad(register.D));
                return 4;
            case 0x63:
                register.H.setValue(cpuRegisterLoad(register.E));
                return 4;
            case 0x64:
                register.H.setValue(cpuRegisterLoad(register.H));
                return 4;
            case 0x65:
                register.H.setValue(cpuRegisterLoad(register.L));
                return 4;
            case 0x68:
                register.L.setValue(cpuRegisterLoad(register.B));
                return 4;
            case 0x69:
                register.L.setValue(cpuRegisterLoad(register.C));
                return 4;
            case 0x6A:
                register.L.setValue(cpuRegisterLoad(register.D));
                return 4;
            case 0x6B:
                register.L.setValue(cpuRegisterLoad(register.E));
                return 4;
            case 0x6C:
                register.L.setValue(cpuRegisterLoad(register.H));
                return 4;
            case 0x6D:
                register.L.setValue(cpuRegisterLoad(register.L));
                return 4;

            /* ROM Loads */
            case 0x7E:
                register.A.setValue(cpuROMLoad(register.getHL()));
                return 8;
            case 0x46:
                register.B.setValue(cpuROMLoad(register.getHL()));
                return 8;
            case 0x4E:
                register.C.setValue(cpuROMLoad(register.getHL()));
                return 8;
            case 0x56:
                register.D.setValue(cpuROMLoad(register.getHL()));
                return 8;
            case 0x5E:
                register.E.setValue(cpuROMLoad(register.getHL()));
                return 8;
            case 0x66:
                register.H.setValue(cpuROMLoad(register.getHL()));
                return 8;
            case 0x6E:
                register.L.setValue(cpuROMLoad(register.getHL()));
                return 8;
            case 0x0A:
                register.A.setValue(cpuROMLoad(register.getBC()));
                return 8;
            case 0x1A:
                register.A.setValue(cpuROMLoad(register.getDE()));
                return 8;
            case 0xFA:
                register.A.setValue(cpuLoadImmediate16BitMemory());
                return 16;
//            case 0x7F:
//                cpuLoadImmediate8BitMemory(register.A);
//                return 8;

            case 0x3E:
                n = memoryManager.readMemory(register.pc.getValue());
                register.pc.inc();
                register.A.setValue(n);
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
                register.B.setValue(cpuRegisterLoad(register.A));
                return 4;
            case 0x4F:
                register.C.setValue(cpuRegisterLoad(register.A));
                return 4;
            case 0x57:
                register.D.setValue(cpuRegisterLoad(register.A));
                return 4;
            case 0x5F:
                register.E.setValue(cpuRegisterLoad(register.A));
                return 4;
            case 0x67:
                register.H.setValue(cpuRegisterLoad(register.A));
                return 4;
            case 0x6F:
                register.L.setValue(cpuRegisterLoad(register.A));
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
                cpuLoadRegisterToImmediateByte(register.A.getValue());
                return 16;

            /* LD A,(0xFF00 + C) */
            case 0xF2:
                register.A.setValue(cpuROMLoad(0xFF00 + register.C.getValue()));
                return 8;

            /* LD (0xFF00 + C), A */
            case 0xE2:
                memoryManager.writeMemory(0xFF00 + register.C.getValue(), register.A.getValue());
                return 8;

            /* Load from memory into A, decrement/increment memory */
            case 0x3A:
                register.A.setValue(cpuROMLoad(register.getHL()));
                register.setHL(cpu16BitDec(register.getHL()));
                return 8;
            case 0x2A:
                register.A.setValue(cpuROMLoad(register.getHL()));
                register.setHL(cpu16BitInc(register.getHL()));
                return 8;

             /* Put A into memory, inc/dec reg */
            case 0x32:
            	cpuLoadRegisterToMemory(register.getHL(), register.A);
            	register.setHL(cpu16BitDec(register.getHL()));
            	return 8;
            	
            /* Load from A into memory increment/decrement register */
            case 0x22:
                cpuLoadRegisterToMemory(register.getHL(), register.A);
                register.setHL(cpu16BitInc(register.getHL()));
                return 8;

            case 0xE0:
                n = memoryManager.readMemory(register.pc.getValue());
                register.pc.inc();
                int address = 0xFF00 + n;
                memoryManager.writeMemory(address, register.A.getValue());
                return 12;

            case 0xF0:
                n = memoryManager.readMemory(register.pc.getValue());
                register.pc.inc();
                register.A.setValue(memoryManager.readMemory(0xFF00 + n));
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
                register.sp.setValue(cpu16BitImmediateLoad());
                return 12;
            case 0xF9:
                register.sp.setValue(register.getHL());
                return 8;
            case 0xF8:
                cpu16BitLDHL();
                return 12;
            case 0x08:
                int nn = memoryManager.readWord();
                register.pc.add(2);
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
            	/*
            	 * When setting AF it is important to zero out the lower for
            	 * bits of the F register because it is *always* zero. The upper
            	 * four bits are are written to when any of the flags need
            	 * setting. So here, the lower four bits are zeroed out and the
            	 * flags are set according to the bits of the upper 4.
            	 */
                register.setAF(memoryManager.pop() & 0xFFF0);
                register.setZ(BitUtil.isSet(register.getF(), 7));
                register.setN(BitUtil.isSet(register.getF(), 6));
                register.setH(BitUtil.isSet(register.getF(), 5));
                register.setC(BitUtil.isSet(register.getF(), 4));
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
                register.A.setValue(cpu8BitAdd(register.A, register.A, false, false));
                return 4;
            case 0x80:
                register.A.setValue(cpu8BitAdd(register.A, register.getBC(), false, false));
                return 4;
            case 0x81:
                register.A.setValue(cpu8BitAdd(register.A, register.C, false, false));
                return 4;
            case 0x82:
                register.A.setValue(cpu8BitAdd(register.A, register.D, false, false));
                return 4;
            case 0x83:
                register.A.setValue(cpu8BitAdd(register.A, register.E, false, false));
                return 4;
            case 0x84:
                register.A.setValue(cpu8BitAdd(register.A, register.H, false, false));
                return 4;
            case 0x85:
                register.A.setValue(cpu8BitAdd(register.A, register.L, false, false));
                return 4;
            case 0x86:
                register.A.setValue(cpu8BitAdd(register.A, memoryManager.readMemory(register.getHL()), false, false));
                return 8;
            case 0xC6:
                register.A.setValue(cpu8BitAdd(register.A, 0, true, false));
                return 8;

            /* 8-bit Add with Carry */
            case 0x8F:
                register.A.setValue(cpu8BitAdd(register.A, register.A, false, true));
                return 4;
            case 0x88:
                register.A.setValue(cpu8BitAdd(register.A, register.getBC(), false, true));
                return 4;
            case 0x89:
                register.A.setValue(cpu8BitAdd(register.A, register.C, false, true));
                return 4;
            case 0x8A:
                register.A.setValue(cpu8BitAdd(register.A, register.D, false, true));
                return 4;
            case 0x8B:
                register.A.setValue(cpu8BitAdd(register.A, register.E, false, true));
                return 4;
            case 0x8C:
                register.A.setValue(cpu8BitAdd(register.A, register.H, false, true));
                return 4;
            case 0x8D:
                register.A.setValue(cpu8BitAdd(register.A, register.L, false, true));
                return 4;
            case 0x8E:
                register.A.setValue(cpu8BitAdd(register.A, memoryManager.readMemory(register.getHL()), false, true));
                return 8;
            case 0xCE:
                register.A.setValue(cpu8BitAdd(register.A, 0, true, true));
                return 8;

            /* 8-bit sub */
            case 0x97:
                register.A.setValue(cpu8BitSub(register.A, register.A, false, false));
                return 4;
            case 0x90:
                register.A.setValue(cpu8BitSub(register.A, register.getBC(), false, false));
                return 4;
            case 0x91:
                register.A.setValue(cpu8BitSub(register.A, register.C, false, false));
                return 4;
            case 0x92:
                register.A.setValue(cpu8BitSub(register.A, register.D, false, false));
                return 4;
            case 0x93:
                register.A.setValue(cpu8BitSub(register.A, register.E, false, false));
                return 4;
            case 0x94:
                register.A.setValue(cpu8BitSub(register.A, register.H, false, false));
                return 4;
            case 0x95:
                register.A.setValue(cpu8BitSub(register.A, register.L, false, false));
                return 4;
            case 0x96:
                register.A.setValue(cpu8BitSub(register.A, memoryManager.readMemory(register.getHL()), false, false));
                return 8;
            case 0xD6:
                register.A.setValue(cpu8BitSub(register.A, 0, true, false));
                return 8;

            /* 8-bit sub with carry */
            case 0x9F:
                register.A.setValue(cpu8BitSub(register.A, register.A, false, true));
                return 4;
            case 0x98:
                register.A.setValue(cpu8BitSub(register.A, register.getBC(), false, true));
                return 4;
            case 0x99:
                register.A.setValue(cpu8BitSub(register.A, register.C, false, true));
                return 4;
            case 0x9A:
                register.A.setValue(cpu8BitSub(register.A, register.D, false, true));
                return 4;
            case 0x9B:
                register.A.setValue(cpu8BitSub(register.A, register.E, false, true));
                return 4;
            case 0x9C:
                register.A.setValue(cpu8BitSub(register.A, register.H, false, true));
                return 4;
            case 0x9D:
                register.A.setValue(cpu8BitSub(register.A, register.L, false, true));
                return 4;
            case 0x9E:
                register.A.setValue(cpu8BitSub(register.A, memoryManager.readMemory(register.getHL()), false, true));
                return 8;

            /* Logical AND */
            case 0xA7:
                register.A.setValue(cpu8BitAND(register.A, register.A, false));
                return 4;
            case 0xA0:
                register.A.setValue(cpu8BitAND(register.A, register.getBC(), false));
                return 4;
            case 0xA1:
                register.A.setValue(cpu8BitAND(register.A, register.C, false));
                return 4;
            case 0xA2:
                register.A.setValue(cpu8BitAND(register.A, register.D, false));
                return 4;
            case 0xA3:
                register.A.setValue(cpu8BitAND(register.A, register.E, false));
                return 4;
            case 0xA4:
                register.A.setValue(cpu8BitAND(register.A, register.H, false));
                return 4;
            case 0xA5:
                register.A.setValue(cpu8BitAND(register.A, register.L, false));
                return 4;
            case 0xA6:
                register.A.setValue(cpu8BitAND(register.A, memoryManager.readMemory(register.getHL()), false));
                return 8;
            case 0xE6:
                register.A.setValue(cpu8BitAND(register.A, 0, true));
                return 8;

            /* Logical OR */
            case 0xB7:
                register.A.setValue(cpu8BitOR(register.A, register.A, false));
                return 4;
            case 0xB0:
                register.A.setValue(cpu8BitOR(register.A, register.getBC(), false));
                return 4;
            case 0xB1:
                register.A.setValue(cpu8BitOR(register.A, register.C, false));
                return 4;
            case 0xB2:
                register.A.setValue(cpu8BitOR(register.A, register.D, false));
                return 4;
            case 0xB3:
                register.A.setValue(cpu8BitOR(register.A, register.E, false));
                return 4;
            case 0xB4:
                register.A.setValue(cpu8BitOR(register.A, register.H, false));
                return 4;
            case 0xB5:
                register.A.setValue(cpu8BitOR(register.A, register.L, false));
                return 4;
            case 0xB6:
                register.A.setValue(cpu8BitOR(register.A, memoryManager.readMemory(register.getHL()), false));
                return 8;
            case 0xF6:
                register.A.setValue(cpu8BitOR(register.A, 0, true));
                return 8;

            /* Logical XOR */
            case 0xAF:
                register.A.setValue(cpu8BitXOR(register.A, register.A, false));
                return 4;
            case 0xA8:
                register.A.setValue(cpu8BitXOR(register.A, register.getBC(), false));
                return 4;
            case 0xA9:
                register.A.setValue(cpu8BitXOR(register.A, register.C, false));
                return 4;
            case 0xAA:
                register.A.setValue(cpu8BitXOR(register.A, register.D, false));
                return 4;
            case 0xAB:
                register.A.setValue(cpu8BitXOR(register.A, register.E, false));
                return 4;
            case 0xAC:
                register.A.setValue(cpu8BitXOR(register.A, register.H, false));
                return 4;
            case 0xAD:
                register.A.setValue(cpu8BitXOR(register.A, register.L, false));
                return 4;
            case 0xAE:
                register.A.setValue(cpu8BitXOR(register.A, memoryManager.readMemory(register.getHL()), false));
                return 8;
            case 0xEE:
                register.A.setValue(cpu8BitXOR(register.A, 0, true));
                return 8;

            /* Compares */
            case 0xBF:
                cpuCompare(register.A, register.A, false);
                return 4;
            case 0xB8:
                cpuCompare(register.A, register.B, false);
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
                register.A.setValue(cpu8BitInc(register.A));
                return 4;
            case 0x04:
                register.B.setValue(cpu8BitInc(register.B));
                return 4;
            case 0x0C:
                register.C.setValue(cpu8BitInc(register.C));
                return 4;
            case 0x14:
                register.D.setValue(cpu8BitInc(register.D));
                return 4;
            case 0x1C:
                register.E.setValue(cpu8BitInc(register.E));
                return 4;
            case 0x24:
                register.H.setValue(cpu8BitInc(register.H));
                return 4;
            case 0x2C:
                register.L.setValue(cpu8BitInc(register.L));
                return 4;
            case 0x34:
                cpuIncMemory(register.getHL());
                return 12;

            /* Decrement */
            case 0x3D:
                register.A.setValue(cpu8BitDec(register.A));
                return 4;
            case 0x05:
                register.B.setValue(cpu8BitDec(register.B));
                return 4;
            case 0x0D:
                register.C.setValue(cpu8BitDec(register.C));
                return 4;
            case 0x15:
                register.D.setValue(cpu8BitDec(register.D));
                return 4;
            case 0x1D:
                register.E.setValue(cpu8BitDec(register.E));
                return 4;
            case 0x25:
                register.H.setValue(cpu8BitDec(register.H));
                return 4;
            case 0x2D:
                register.L.setValue(cpu8BitDec(register.L));
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
                register.setHL( cpu16BitAdd(register.getHL(), register.sp.getValue()));
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
                register.sp.setValue(cpu16BitInc(register.sp.getValue()));
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
                register.sp.setValue(cpu16BitDec(register.sp.getValue()));
                return 8;

            /* DAA */
            case 0x27:
                cpuDAA();
                return 4;

            /* Miscellaneous */
            case 0x2F:
                register.A.setValue(~register.A.getValue());
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
                register.pc.inc();
                return 4;

            case 0xF3:
                this.interruptMaster = false;
                return 4;

            case 0xFB:
                this.pendingInterruptEnabled = true;
                return 4;

            /* Rotates and shifts */
            case 0x07:
                register.A.setValue(cpuRLC(register.A));
                return 4;

            case 0x17:
                register.A.setValue(cpuRL(register.A));
                return 4;

            case 0x0F:
                register.A.setValue(cpuRRC(register.A));
                return 4;

            case 0x1F:
                register.A.setValue(cpuRR(register.A));
                register.clearZ();
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
                register.pc.setValue(register.getHL());
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
                return register.isC() ? 8 : 12;
            case 0x38:
                cpuJumpImmediate(true, Register2.FLAG_C, true);
                return 8;

            /* Calls */
            case 0xCD:
                cpuCall(false, 0, false);
                return 24;
            case 0xC4:
                cpuCall(true, Register2.FLAG_Z, false);
                return register.isZ() ? 12 : 24;
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
                return register.isC() ? 8 : 20;
            case 0xD8:
                cpuReturn(true, Register2.FLAG_C, true);
                return register.isC() ? 20 : 8;

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
//                throw new Exception("Unrecognised opcode: " + hexCode);
        }
        return 0;
    }

    private int executeExtendedOpcode() {
        /*
         * When the opcode 0xCB is encountered the next immediate byte needs to be
         * decoded and treated as an opcode.
         */
        int opcode = memoryManager.readMemory(register.pc.getValue());
        register.pc.inc();

        switch (opcode) {
        /* Swaps */
        case 0x37:
            register.A.setValue(cpuSwapNibbles(register.A));
            return 8;
        case 0x30:
            register.B.setValue(cpuSwapNibbles(register.B));
            return 8;
        case 0x31:
            register.C.setValue(cpuSwapNibbles(register.C));
            return 8;
        case 0x32:
            register.D.setValue(cpuSwapNibbles(register.D));
            return 8;
        case 0x33:
            register.E.setValue(cpuSwapNibbles(register.E));
            return 8;
        case 0x34:
            register.H.setValue(cpuSwapNibbles(register.H));
            return 8;
        case 0x35:
            register.L.setValue(cpuSwapNibbles(register.L));
            return 8;

        /* Rotate left through carry */
        case 0x07:
            register.A.setValue(cpuRLC(register.A));
            return 8;
        case 0x00:
            register.B.setValue(cpuRLC(register.B));
            return 8;
        case 0x01:
            register.C.setValue(cpuRLC(register.C));
            return 8;
        case 0x02:
            register.D.setValue(cpuRLC(register.D));
            return 8;
        case 0x03:
            register.E.setValue(cpuRLC(register.E));
            return 8;
        case 0x04:
            register.H.setValue(cpuRLC(register.H));
            return 8;
        case 0x05:
            register.L.setValue(cpuRLC(register.L));
            return 8;
        case 0x06:
            cpuRLCMemory(register.getHL());
            return 16;

        /* rotate left */
        case 0x17:
            register.A.setValue(cpuRL(register.A));
            return 8;
        case 0x10:
            register.B.setValue(cpuRL(register.B));
            return 8;
        case 0x11:
            register.C.setValue(cpuRL(register.C));
            return 8;
        case 0x12:
            register.D.setValue(cpuRL(register.D));
            return 8;
        case 0x13:
            register.E.setValue(cpuRL(register.E));
            return 8;
        case 0x14:
            register.H.setValue(cpuRL(register.H));
            return 8;
        case 0x15:
            register.L.setValue(cpuRL(register.A));
            return 8;
        case 0x16:
            cpuRLMemory(register.getHL());
            return 16;

        /* Rotate right through carry */
        case 0x0F:
            register.A.setValue(cpuRRC(register.A));
            return 8;
        case 0x08:
            register.B.setValue(cpuRRC(register.B));
            return 8;
        case 0x09:
            register.C.setValue(cpuRRC(register.C));
            return 8;
        case 0x0A:
            register.D.setValue(cpuRRC(register.D));
            return 8;
        case 0x0B:
            register.E.setValue(cpuRRC(register.E));
            return 8;
        case 0x0C:
            register.H.setValue(cpuRRC(register.H));
            return 8;
        case 0x0D:
            register.L.setValue(cpuRRC(register.L));
            return 8;
        case 0x0E:
            cpuRRCMemory(register.getHL());
            return 16;

        /* rotate right */
        case 0x1F:
            register.A.setValue(cpuRR(register.A));
            return 8;
        case 0x18:
            register.B.setValue(cpuRR(register.B));
            return 8;
        case 0x19:
            register.C.setValue(cpuRR(register.C));
            return 8;
        case 0x1A:
            register.D.setValue(cpuRR(register.D));
            return 8;
        case 0x1B:
            register.E.setValue(cpuRR(register.E));
            return 8;
        case 0x1C:
            register.H.setValue(cpuRR(register.H));
            return 8;
        case 0x1D:
            register.L.setValue(cpuRR(register.L));
            return 8;
        case 0x1E:
            cpuRRMemory(register.getHL());
            return 16;

        /* shift left */
        case 0x27:
            register.A.setValue(cpuSLA(register.A));
            return 8;
        case 0x20:
            register.B.setValue(cpuSLA(register.B));
            return 8;
        case 0x21:
            register.C.setValue(cpuSLA(register.C));
            return 8;
        case 0x22:
            register.D.setValue(cpuSLA(register.D));
            return 8;
        case 0x23:
            register.E.setValue(cpuSLA(register.E));
            return 8;
        case 0x24:
            register.H.setValue(cpuSLA(register.H));
            return 8;
        case 0x25:
            register.L.setValue(cpuSLA(register.L));
            return 8;
        case 0x26:
            cpuSLAMemory(register.H.getValue());
            return 16;

        /* shift right into carry */
        case 0x2F:
            register.A.setValue(cpuSRA(register.A.getValue()));
            return 8;
        case 0x28:
            register.B.setValue(cpuSRA(register.B.getValue()));
            return 8;
        case 0x29:
            register.C.setValue(cpuSRA(register.C.getValue()));
            return 8;
        case 0x2A:
            register.D.setValue(cpuSRA(register.D.getValue()));
            return 8;
        case 0x2B:
            register.E.setValue(cpuSRA(register.E.getValue()));
            return 8;
        case 0x2C:
            register.H.setValue(cpuSRA(register.H.getValue()));
            return 8;
        case 0x2D:
            register.L.setValue(cpuSRA(register.L.getValue()));
            return 8;
        case 0x2E:
            cpuSRAMemory(register.getHL());
            return 16;
        case 0x3F:
            register.A.setValue(cpuSRL(register.A.getValue()));
            return 8;
        case 0x38:
            register.B.setValue(cpuSRL(register.B.getValue()));
            return 8;
        case 0x39:
            register.C.setValue(cpuSRL(register.C.getValue()));
            return 8;
        case 0x3A:
            register.D.setValue(cpuSRL(register.D.getValue()));
            return 8;
        case 0x3B:
            register.E.setValue(cpuSRL(register.E.getValue()));
            return 8;
        case 0x3C:
            register.H.setValue(cpuSRL(register.H.getValue()));
            return 8;
        case 0x3D:
            register.L.setValue(cpuSRL(register.L.getValue()));
            return 8;
        case 0x3E:
            cpuSRLMemory(register.getHL());
            return 16;

        /* Test Bit */
        case 0x40:
            cpuTestBit(register.getBC(), 0);
            return 8;
        case 0x41:
            cpuTestBit(register.C.getValue(), 0);
            return 8;
        case 0x42:
            cpuTestBit(register.D.getValue(), 0);
            return 8;
        case 0x43:
            cpuTestBit(register.E.getValue(), 0);
            return 8;
        case 0x44:
            cpuTestBit(register.H.getValue(), 0);
            return 8;
        case 0x45:
            cpuTestBit(register.L.getValue(), 0);
            return 8;
        case 0x46:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 0);
            return 16;
        case 0x47:
            cpuTestBit(register.A.getValue(), 0);
            return 8;
        case 0x48:
            cpuTestBit(register.getBC(), 1);
            return 8;
        case 0x49:
            cpuTestBit(register.C.getValue(), 1);
            return 8;
        case 0x4A:
            cpuTestBit(register.D.getValue(), 1);
            return 8;
        case 0x4B:
            cpuTestBit(register.E.getValue(), 1);
            return 8;
        case 0x4C:
            cpuTestBit(register.H.getValue(), 1);
            return 8;
        case 0x4D:
            cpuTestBit(register.L.getValue(), 1);
            return 8;
        case 0x4E:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 1);
            return 16;
        case 0x4F:
            cpuTestBit(register.A.getValue(), 1);
            return 8;
        case 0x50:
            cpuTestBit(register.getBC(), 2);
            return 8;
        case 0x51:
            cpuTestBit(register.C.getValue(), 2);
            return 8;
        case 0x52:
            cpuTestBit(register.D.getValue(), 2);
            return 8;
        case 0x53:
            cpuTestBit(register.E.getValue(), 2);
            return 8;
        case 0x54:
            cpuTestBit(register.H.getValue(), 2);
            return 8;
        case 0x55:
            cpuTestBit(register.L.getValue(), 2);
            return 8;
        case 0x56:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 2);
            return 16;
        case 0x57:
            cpuTestBit(register.A.getValue(), 2);
            return 8;
        case 0x58:
            cpuTestBit(register.getBC(), 3);
            return 8;
        case 0x59:
            cpuTestBit(register.C.getValue(), 3);
            return 8;
        case 0x5A:
            cpuTestBit(register.D.getValue(), 3);
            return 8;
        case 0x5B:
            cpuTestBit(register.E.getValue(), 3);
            return 8;
        case 0x5C:
            cpuTestBit(register.H.getValue(), 3);
            return 8;
        case 0x5D:
            cpuTestBit(register.L.getValue(), 3);
            return 8;
        case 0x5E:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 3);
            return 16;
        case 0x5F:
            cpuTestBit(register.A.getValue(), 3);
            return 8;
        case 0x60:
            cpuTestBit(register.getBC(), 4);
            return 8;
        case 0x61:
            cpuTestBit(register.C.getValue(), 4);
            return 8;
        case 0x62:
            cpuTestBit(register.D.getValue(), 4);
            return 8;
        case 0x63:
            cpuTestBit(register.E.getValue(), 4);
            return 8;
        case 0x64:
            cpuTestBit(register.H.getValue(), 4);
            return 8;
        case 0x65:
            cpuTestBit(register.L.getValue(), 4);
            return 8;
        case 0x66:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 4);
            return 16;
        case 0x67:
            cpuTestBit(register.A.getValue(), 4);
            return 8;
        case 0x68:
            cpuTestBit(register.getBC(), 5);
            return 8;
        case 0x69:
            cpuTestBit(register.C.getValue(), 5);
            return 8;
        case 0x6A:
            cpuTestBit(register.D.getValue(), 5);
            return 8;
        case 0x6B:
            cpuTestBit(register.E.getValue(), 5);
            return 8;
        case 0x6C:
            cpuTestBit(register.H.getValue(), 5);
            return 8;
        case 0x6D:
            cpuTestBit(register.L.getValue(), 5);
            return 8;
        case 0x6E:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 5);
            return 16;
        case 0x6F:
            cpuTestBit(register.A.getValue(), 5);
            return 8;
        case 0x70:
            cpuTestBit(register.getBC(), 6);
            return 8;
        case 0x71:
            cpuTestBit(register.C.getValue(), 6);
            return 8;
        case 0x72:
            cpuTestBit(register.D.getValue(), 6);
            return 8;
        case 0x73:
            cpuTestBit(register.E.getValue(), 6);
            return 8;
        case 0x74:
            cpuTestBit(register.H.getValue(), 6);
            return 8;
        case 0x75:
            cpuTestBit(register.L.getValue(), 6);
            return 8;
        case 0x76:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 6);
            return 16;
        case 0x77:
            cpuTestBit(register.A.getValue(), 6);
            return 8;
        case 0x78:
            cpuTestBit(register.getBC(), 7);
            return 8;
        case 0x79:
            cpuTestBit(register.C.getValue(), 7);
            return 8;
        case 0x7A:
            cpuTestBit(register.D.getValue(), 7);
            return 8;
        case 0x7B:
            cpuTestBit(register.E.getValue(), 7);
            return 8;
        case 0x7C:
            cpuTestBit(register.H.getValue(), 7);
            return 8;
        case 0x7D:
            cpuTestBit(register.L.getValue(), 7);
            return 8;
        case 0x7E:
            cpuTestBit(memoryManager.readMemory(register.getHL()), 7);
            return 16;
        case 0x7F:
            cpuTestBit(register.A.getValue(), 7);
            return 8;

        /* set bit */
        case 0xC0:
            register.B.setValue(cpuSetBit(register.getBC(), 0));
            return 8;
        case 0xC1:
            register.C.setValue(cpuSetBit(register.C.getValue(), 0));
            return 8;
        case 0xC2:
            register.D.setValue(cpuSetBit(register.D.getValue(), 0));
            return 8;
        case 0xC3:
            register.E.setValue(cpuSetBit(register.E.getValue(), 0));
            return 8;
        case 0xC4:
            register.H.setValue(cpuSetBit(register.H.getValue(), 0));
            return 8;
        case 0xC5:
            register.L.setValue(cpuSetBit(register.L.getValue(), 0));
            return 8;
        case 0xC6:
            cpuSetBitMemory(register.getHL(), 0);
            return 16;
        case 0xC7:
            register.A.setValue(cpuSetBit(register.A.getValue(), 0));
            return 8;
        case 0xC8:
            register.B.setValue(cpuSetBit(register.getBC(), 1));
            return 8;
        case 0xC9:
            register.C.setValue(cpuSetBit(register.C.getValue(), 1));
            return 8;
        case 0xCA:
            register.D.setValue(cpuSetBit(register.D.getValue(), 1));
            return 8;
        case 0xCB:
            register.E.setValue(cpuSetBit(register.E.getValue(), 1));
            return 8;
        case 0xCC:
            register.H.setValue(cpuSetBit(register.H.getValue(), 1));
            return 8;
        case 0xCD:
            register.L.setValue(cpuSetBit(register.L.getValue(), 1));
            return 8;
        case 0xCE:
            cpuSetBitMemory(register.getHL(), 1);
            return 16;
        case 0xCF:
            register.A.setValue(cpuSetBit(register.A.getValue(), 1));
            return 8;
        case 0xD0:
            register.B.setValue(cpuSetBit(register.getBC(), 2));
            return 8;
        case 0xD1:
            register.C.setValue(cpuSetBit(register.C.getValue(), 2));
            return 8;
        case 0xD2:
            register.D.setValue(cpuSetBit(register.D.getValue(), 2));
            return 8;
        case 0xD3:
            register.E.setValue(cpuSetBit(register.E.getValue(), 2));
            return 8;
        case 0xD4:
            register.H.setValue(cpuSetBit(register.H.getValue(), 2));
            return 8;
        case 0xD5:
            register.L.setValue(cpuSetBit(register.L.getValue(), 2));
            return 8;
        case 0xD6:
            cpuSetBitMemory(register.getHL(), 2);
            return 16;
        case 0xD7:
            register.A.setValue(cpuSetBit(register.A.getValue(), 2));
            return 8;
        case 0xD8:
            register.B.setValue(cpuSetBit(register.getBC(), 3));
            return 8;
        case 0xD9:
            register.C.setValue(cpuSetBit(register.C.getValue(), 3));
            return 8;
        case 0xDA:
            register.D.setValue(cpuSetBit(register.D.getValue(), 3));
            return 8;
        case 0xDB:
            register.E.setValue(cpuSetBit(register.E.getValue(), 3));
            return 8;
        case 0xDC:
            register.H.setValue(cpuSetBit(register.H.getValue(), 3));
            return 8;
        case 0xDD:
            register.L.setValue(cpuSetBit(register.L.getValue(), 3));
            return 8;
        case 0xDE:
            cpuSetBitMemory(register.getHL(), 3);
            return 16;
        case 0xDF:
            register.A.setValue(cpuSetBit(register.A.getValue(), 3));
            return 8;
        case 0xE0:
            register.B.setValue(cpuSetBit(register.getBC(), 4));
            return 8;
        case 0xE1:
            register.C.setValue(cpuSetBit(register.C.getValue(), 4));
            return 8;
        case 0xE2:
            register.D.setValue(cpuSetBit(register.D.getValue(), 4));
            return 8;
        case 0xE3:
            register.E.setValue(cpuSetBit(register.E.getValue(), 4));
            return 8;
        case 0xE4:
            register.H.setValue(cpuSetBit(register.H.getValue(), 4));
            return 8;
        case 0xE5:
            register.L.setValue(cpuSetBit(register.L.getValue(), 4));
            return 8;
        case 0xE6:
            cpuSetBitMemory(register.getHL(), 4);
            return 16;
        case 0xE7:
            register.A.setValue(cpuSetBit(register.A.getValue(), 4));
            return 8;
        case 0xE8:
            register.B.setValue(cpuSetBit(register.getBC(), 5));
            return 8;
        case 0xE9:
            register.C.setValue(cpuSetBit(register.C.getValue(), 5));
            return 8;
        case 0xEA:
            register.D.setValue(cpuSetBit(register.D.getValue(), 5));
            return 8;
        case 0xEB:
            register.E.setValue(cpuSetBit(register.E.getValue(), 5));
            return 8;
        case 0xEC:
            register.H.setValue(cpuSetBit(register.H.getValue(), 5));
            return 8;
        case 0xED:
            register.L.setValue(cpuSetBit(register.L.getValue(), 5));
            return 8;
        case 0xEE:
            cpuSetBitMemory(register.getHL(), 5);
            return 16;
        case 0xEF:
            register.A.setValue(cpuSetBit(register.A.getValue(), 5));
            return 8;
        case 0xF0:
            register.B.setValue(cpuSetBit(register.getBC(), 6));
            return 8;
        case 0xF1:
            register.C.setValue(cpuSetBit(register.getBC(), 6));
            return 8;
        case 0xF2:
            register.D.setValue(cpuSetBit(register.D.getValue(), 6));
            return 8;
        case 0xF3:
            register.E.setValue(cpuSetBit(register.E.getValue(), 6));
            return 8;
        case 0xF4:
            register.H.setValue(cpuSetBit(register.H.getValue(), 6));
            return 8;
        case 0xF5:
            register.L.setValue(cpuSetBit(register.L.getValue(), 6));
            return 8;
        case 0xF6:
            cpuSetBitMemory(register.getHL(), 6);
            return 16;
        case 0xF7:
            register.A.setValue(cpuSetBit(register.A.getValue(), 6));
            return 8;
        case 0xF8:
            register.B.setValue(cpuSetBit(register.getBC(), 7));
            return 8;
        case 0xF9:
            register.C.setValue(cpuSetBit(register.C.getValue(), 7));
            return 8;
        case 0xFA:
            register.D.setValue(cpuSetBit(register.D.getValue(), 7));
            return 8;
        case 0xFB:
            register.E.setValue(cpuSetBit(register.E.getValue(), 7));
            return 8;
        case 0xFC:
            register.H.setValue(cpuSetBit(register.H.getValue(), 7));
            return 8;
        case 0xFD:
            register.L.setValue(cpuSetBit(register.L.getValue(), 7));
            return 8;
        case 0xFE:
            cpuSetBitMemory(register.getHL(), 7);
            return 16;
        case 0xFF:
            register.A.setValue(cpuSetBit(register.A.getValue(), 7));
            return 8;

        /* reset bit */
        case 0x80:
            register.B.setValue(cpuResetBit(register.getBC(), 0));
            return 8;
        case 0x81:
            register.C.setValue(cpuResetBit(register.C.getValue(), 0));
            return 8;
        case 0x82:
            register.D.setValue(cpuResetBit(register.D.getValue(), 0));
            return 8;
        case 0x83:
            register.E.setValue(cpuResetBit(register.E.getValue(), 0));
            return 8;
        case 0x84:
            register.H.setValue(cpuResetBit(register.H.getValue(), 0));
            return 8;
        case 0x85:
            register.L.setValue(cpuResetBit(register.L.getValue(), 0));
            return 8;
        case 0x86:
            cpuResetBitMemory(register.getHL(), 0);
            return 16;
        case 0x87:
            register.A.setValue(cpuResetBit(register.A.getValue(), 0));
            return 8;
        case 0x88:
            register.B.setValue(cpuResetBit(register.getBC(), 1));
            return 8;
        case 0x89:
            register.C.setValue(cpuResetBit(register.C.getValue(), 1));
            return 8;
        case 0x8A:
            register.D.setValue(cpuResetBit(register.D.getValue(), 1));
            return 8;
        case 0x8B:
            register.E.setValue(cpuResetBit(register.E.getValue(), 1));
            return 8;
        case 0x8C:
            register.H.setValue(cpuResetBit(register.H.getValue(), 1));
            return 8;
        case 0x8D:
            register.L.setValue(cpuResetBit(register.L.getValue(), 1));
            return 8;
        case 0x8E:
            cpuResetBitMemory(register.getHL(), 1);
            return 16;
        case 0x8F:
            register.A.setValue(cpuResetBit(register.A.getValue(), 1));
            return 8;
        case 0x90:
            register.B.setValue(cpuResetBit(register.getBC(), 2));
            return 8;
        case 0x91:
            register.C.setValue(cpuResetBit(register.C.getValue(), 2));
            return 8;
        case 0x92:
            register.D.setValue(cpuResetBit(register.D.getValue(), 2));
            return 8;
        case 0x93:
            register.E.setValue(cpuResetBit(register.E.getValue(), 2));
            return 8;
        case 0x94:
            register.H.setValue(cpuResetBit(register.H.getValue(), 2));
            return 8;
        case 0x95:
            register.L.setValue(cpuResetBit(register.L.getValue(), 2));
            return 8;
        case 0x96:
            cpuResetBitMemory(register.getHL(), 2);
            return 16;
        case 0x97:
            register.A.setValue(cpuResetBit(register.A.getValue(), 2));
            return 8;
        case 0x98:
            register.B.setValue(cpuResetBit(register.getBC(), 3));
            return 8;
        case 0x99:
            register.C.setValue(cpuResetBit(register.C.getValue(), 3));
            return 8;
        case 0x9A:
            register.D.setValue(cpuResetBit(register.D.getValue(), 3));
            return 8;
        case 0x9B:
            register.E.setValue(cpuResetBit(register.E.getValue(), 3));
            return 8;
        case 0x9C:
            register.H.setValue(cpuResetBit(register.H.getValue(), 3));
            return 8;
        case 0x9D:
            register.L.setValue(cpuResetBit(register.L.getValue(), 3));
            return 8;
        case 0x9E:
            cpuResetBitMemory(register.getHL(), 3);
            return 16;
        case 0x9F:
            register.A.setValue(cpuResetBit(register.A.getValue(), 3));
            return 8;
        case 0xA0:
            register.B.setValue(cpuResetBit(register.getBC(), 4));
            return 8;
        case 0xA1:
            register.C.setValue(cpuResetBit(register.C.getValue(), 4));
            return 8;
        case 0xA2:
            register.D.setValue(cpuResetBit(register.D.getValue(), 4));
            return 8;
        case 0xA3:
            register.E.setValue(cpuResetBit(register.E.getValue(), 4));
            return 8;
        case 0xA4:
            register.H.setValue(cpuResetBit(register.H.getValue(), 4));
            return 8;
        case 0xA5:
            register.L.setValue(cpuResetBit(register.L.getValue(), 4));
            return 8;
        case 0xA6:
            cpuResetBitMemory(register.getHL(), 4);
            return 16;
        case 0xA7:
            register.A.setValue(cpuResetBit(register.A.getValue(), 4));
            return 8;
        case 0xA8:
            register.B.setValue(cpuResetBit(register.getBC(), 5));
            return 8;
        case 0xA9:
            register.C.setValue(cpuResetBit(register.C.getValue(), 5));
            return 8;
        case 0xAA:
            register.D.setValue(cpuResetBit(register.D.getValue(), 5));
            return 8;
        case 0xAB:
            register.E.setValue(cpuResetBit(register.E.getValue(), 5));
            return 8;
        case 0xAC:
            register.H.setValue(cpuResetBit(register.H.getValue(), 5));
            return 8;
        case 0xAD:
            register.L.setValue(cpuResetBit(register.L.getValue(), 5));
            return 8;
        case 0xAE:
            cpuResetBitMemory(register.getHL(), 5);
            return 16;
        case 0xAF:
            register.A.setValue(cpuResetBit(register.A.getValue(), 5));
            return 8;
        case 0xB0:
            register.B.setValue(cpuResetBit(register.getBC(), 6));
            return 8;
        case 0xB1:
            register.C.setValue(cpuResetBit(register.C.getValue(), 6));
            return 8;
        case 0xB2:
            register.D.setValue(cpuResetBit(register.D.getValue(), 6));
            return 8;
        case 0xB3:
            register.E.setValue(cpuResetBit(register.E.getValue(), 6));
            return 8;
        case 0xB4:
            register.H.setValue(cpuResetBit(register.H.getValue(), 6));
            return 8;
        case 0xB5:
            register.L.setValue(cpuResetBit(register.L.getValue(), 6));
            return 8;
        case 0xB6:
            cpuResetBitMemory(register.getHL(), 6);
            return 16;
        case 0xB7:
            register.A.setValue(cpuResetBit(register.A.getValue(), 6));
            return 8;
        case 0xB8:
            register.B.setValue(cpuResetBit(register.getBC(), 7));
            return 8;
        case 0xB9:
            register.C.setValue(cpuResetBit(register.C.getValue(), 7));
            return 8;
        case 0xBA:
            register.D.setValue(cpuResetBit(register.D.getValue(), 7));
            return 8;
        case 0xBB:
            register.E.setValue(cpuResetBit(register.E.getValue(), 7));
            return 8;
        case 0xBC:
            register.H.setValue(cpuResetBit(register.H.getValue(), 7));
            return 8;
        case 0xBD:
            register.L.setValue(cpuResetBit(register.L.getValue(), 7));
            return 8;
        case 0xBE:
            cpuResetBitMemory(register.getHL(), 7);
            return 16;
        case 0xBF:
            register.A.setValue(cpuResetBit(register.A.getValue(), 7));
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
            if (register.isC() || register.A.getValue() > 0x99) {
                register.A.add(0x60); //= register.A + 0x60;
                register.setC();
            }

            if (register.isH() || ((register.A.getValue() & 0x0F) > 0x9)) {
                register.A.add(0x06);// = register.A + 0x6;
                register.clearH();
            } 
        }
        
        else if (register.isC() && register.isH()) {
                    register.A.add(0x9A);// = register.A - 0x60;
                    register.clearH();
        } else if (register.isC()) {
                    register.A.add(0xA0);// = register.A - 0x6;
        }else if (register.isH()) {
        	register.A.add(0xFA);
        	register.clearH();
        }

        register.setZ(register.A.getValue() == 0);
        //register.clearH();
    }

    private void cpuRestart(int n) {
        memoryManager.push(register.pc.getValue());
        register.pc.setValue(n);
    }

    private void cpuCall(boolean useCondition, int flag, boolean condition) {
        int word = memoryManager.readWord();
        /*
         * Advance 2 positions ahead because two bytes were just read.
         */
        register.pc.add(2);//;= register.pc + 2;

        if (!useCondition) {
            memoryManager.push(register.pc.getValue());
            register.pc.setValue(word);// = word;
            return;
        }

        if (BitUtil.isSet(register.F.getValue(), flag) == condition) {
            memoryManager.push(register.pc.getValue());
            register.pc.setValue(word);// = word;
        }
    }

    private void cpuReturn(boolean useCondition, int flag, boolean condition) {
        if (!useCondition) {
            register.pc.setValue(memoryManager.pop());
            return;
        }

        if (BitUtil.isSet(register.F.getValue(), flag) == condition) {
            register.pc.setValue(memoryManager.pop());
        }
    }

    private void cpuReturnFromInterrupt() {
        register.pc.setValue(memoryManager.pop());
        interruptMaster = true;
    }

    private void cpuJump(boolean useJumpCondition, int flag, boolean jumpCondition) {
        int nn = memoryManager.readWord();
        register.pc.add(2);// = register.pc + 2;

        if (!useJumpCondition) {
            register.pc.setValue(nn);
            return;
        }

        if (BitUtil.isSet(register.F.getValue(), flag) == jumpCondition) {
            register.pc.setValue(nn);
        }
    }

//            case 0x30:
//                cpuJumpImmediate(true, Register2.FLAG_C, false);
//                return register.isC() ? 8 : 12;
//    public void cpuJumpImmediate(boolean useCondition, int flag, boolean condition) {
//        if (!useCondition) {
//        int address = memoryManager.readMemory(register.pc.getValue()) + register.pc.getValue();
//        register.pc.inc();
//            /*
//             * Jump unconditionally
//             */
//            register.pc.setValue(address);// = register.pc + n;
//        } else if (BitUtil.isSet(register.F.getValue(), flag) == condition) {
//        byte n = (byte) memoryManager.readMemory(register.pc.getValue());
//        register.pc.inc();
//            /*
//             * Only jump if the condition is met
//             */
//            register.pc.setValue(register.pc.getValue() + n);// = register.pc + n;
//        	//Debug.print("Value assigned: " + ((int)register.pc.getValue() + (int)n), 1268960, true);
//        }
//        //register.pc.inc();
//    }
    
    public void cpuJumpImmediate(boolean useCondition, int flag, boolean condition) {
        byte n = (byte) memoryManager.readMemory(register.pc.getValue());

        if (!useCondition) {
            /*
             * Jump unconditionally
             */
            register.pc.add(n);// = register.pc + n;
        } else if (BitUtil.isSet(register.F.getValue(), flag) == condition) {
            /*
             * Only jump if the condition is met
             */
            register.pc.add(n);// = register.pc + n;
        }
        register.pc.inc();
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

    private int cpuSLA(UInt reg) {
        register.setC(BitUtil.isSet(reg.getValue(), 7));
        reg.setValue((reg.getValue() << 1) & 0xFF);
        register.clearN();
        register.clearH();
        register.setZ(reg.getValue() == 0);
        return reg.getValue();
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

    private int cpuRLC(UInt reg) {
        int n = reg.getValue() & 0xFF;
        register.A.setValue((reg.getValue() << 1 | reg.getValue() >> 7) & 0xFF);
        register.setZ(reg.getValue() == 0);
        register.clearN();
        register.clearH();
        register.setC(BitUtil.isSet(n, 7));

        return reg.getValue();
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

    private int cpuRL(UInt reg) {
        boolean carrySet = register.isC();
        boolean isMSBSet = BitUtil.isSet(reg.getValue(), 7);

        reg.setValue((reg.getValue() << 1) & 0xFF);

        register.clearH();
        register.clearN();
        register.setC(isMSBSet);

        if (carrySet) {
            reg.setValue(BitUtil.setBit(reg.getValue(), 0));
        }

        register.setZ(reg.getValue() == 0);

        return reg.getValue();
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

    private int cpuRRC(UInt reg) {
        boolean isLSBSet = BitUtil.isSet(reg.getValue(), 0);

        register.clearAllFlags();

        reg.setValue((reg.getValue() >> 1) & 0xFF);

        if (isLSBSet) {
            register.setC();
            reg.setValue(BitUtil.setBit(reg.getValue(), 7));
        }

        if (reg.getValue() == 0) {
            register.setZ();
        }

        return reg.getValue();
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

    private int cpuRR(UInt reg) {
        boolean carrySet = register.isC();
        boolean isLSBSet = BitUtil.isSet(reg.getValue(), 0);

        reg.setValue((reg.getValue() >> 1) & 0xFF);

        register.clearH();
        register.clearN();
        register.setC(isLSBSet);

        if (carrySet) {
            reg.setValue(BitUtil.setBit(reg.getValue(), 7));
        }

        register.setZ(reg.getValue() == 0);

        return reg.getValue();
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

    private int cpuSwapNibbles(UInt n) {
        n.setValue((((n.getValue() & 0xF0) >> 4) | ((n.getValue() & 0x0F) << 4)));

        register.clearAllFlags();
        register.setZ(n.getValue() == 0);
        return n.getValue();
    }

    private int cpu16BitImmediateLoad() {
        int nn = memoryManager.readWord();
        register.pc.add(2);// += 2;
        return nn;
    }

    private void cpuCompare(UInt A, UInt val, boolean useImmediate) {
        cpuCompare(A, val.getValue(), useImmediate);
    }

    private void cpuCompare(UInt A, int val, boolean useImmediate) {
        int n = 0;

        if (useImmediate) {
            n = memoryManager.readMemory(register.pc.getValue());
            register.pc.inc();
        } else {
            n = val;
        }

        register.setZ(A.getValue() == n);
        register.setC(A.getValue() < n);
        register.setH((n & 0x0F) > (A.getValue() & 0x0F));
        register.setN(true);
    }

    private int cpu8BitInc(UInt n) {
        int initialN = n.getValue();
        n.inc();
//        n++;
//        if(n == 256) {
//            n = 0;
//        }
        
        //System.out.println("N after: " + n);
        register.setZ(n.getValue() == 0);
        register.setN(false);
        register.setH(((initialN & 0x0F) + (1 & 0x0F)) > 0x0F);
        return n.getValue();
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

    private int cpu8BitDec(UInt n) {
        int initialN = n.getValue();
        n.dec();
        register.setZ(n.getValue() == 0);
        register.setN(true);
        register.setH((initialN & 0x0F) == 0);
        return n.getValue();
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
    	//Problems occurred!!!
//        int n = memoryManager.readMemory(register.sp.getValue());
        int val1 = register.sp.getValue();
        int val2 = (byte)memoryManager.readMemory(register.pc.getValue());
        register.pc.inc();
        int result = val1 + val2;
        //Debug.print(String.format("val2 = %d", val2), 174951, false);

        register.setHL(result & 0xFFFF);
        //.Debug.print(String.format("result = %d", result & 0xFFFF), 174951, true);

        register.clearZ();
        register.clearN();
        
        int temp = val1 ^ val2 ^ result;
        register.setH((temp & 0x10) == 0x10);
        register.setC((temp & 0x100) == 0x100);

//        if (result > 0xFFFF) {
//            register.setC();
//        } else {
//            register.clearC();
//        }
//
//        if ((register.sp.getValue() & 0xF) + (val1 & 0xF) > 0xF) {
//            register.setH();
//        } else {
//            register.clearH();
//        }
    }

    private int cpu8BitAND(UInt reg1, UInt reg2, boolean useImmediate) {
        return cpu8BitAND(reg1, reg2.getValue(), useImmediate);
    }

    private int cpu8BitAND(UInt reg, int value, boolean useImmediate) {
        int n = 0;

        if (useImmediate) {
            n = memoryManager.readMemory(register.pc.getValue());
            register.pc.inc();
        } else {
            n = value;
        }

        reg.setValue(reg.getValue() & n);

        register.clearN();
        register.setH();
        register.clearC();
        register.setZ(reg.getValue() == 0x0);

        return reg.getValue();
    }

    private int cpu8BitOR(UInt reg1, UInt reg2, boolean useImmediate) {
        return cpu8BitOR(reg1, reg2.getValue(), useImmediate);
    }

    private int cpu8BitOR(UInt reg, int value, boolean useImmediate) {
        int n = 0;

        if (useImmediate) {
            n = memoryManager.readMemory(register.pc.getValue());
            register.pc.inc();
        } else {
            n = value;
        }

        reg.setValue(reg.getValue() | n);

        register.clearN();
        register.clearH();
        register.clearC();

        register.setZ(reg.getValue() == 0);

        return reg.getValue();
    }

    private int cpu8BitLoad() {
        int n = memoryManager.readMemory(register.pc.getValue());
        register.pc.inc();
        return n;
    }

    private int cpuLoadImmediate16BitMemory() {
        int nn = memoryManager.readWord();
        register.pc.add(2); /* Memory is stored in bytes and 1 word (2 bytes) are read */
        int n = memoryManager.readMemory(nn);
        return n;
    }

    private void cpuLoadRegisterToImmediateByte(int reg) {
        int nn = memoryManager.readWord();
        register.pc.add(2);
        memoryManager.writeMemory(nn, reg);
    }

    private int cpuRegisterLoad(UInt register) {
        return register.getValue();
    }

    private void cpuLoadImmediate8BitMemory(int destination) {
        int data = memoryManager.readMemory(register.pc.getValue());
        register.pc.inc();
        memoryManager.writeMemory(destination, data);
    }

    private void cpuLoadRegisterToMemory(int address, UInt data) {
        memoryManager.writeMemory(address, data.getValue());
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

    private int cpu8BitAdd(UInt reg1, UInt reg2, boolean addImmediate, boolean addCarry) {
        return cpu8BitAdd(reg1, reg2.getValue(), addImmediate, addCarry);
    }

    private int cpu8BitAdd(UInt reg1, int value, boolean addImmediate, boolean addCarry) {
        int initialValue = reg1.getValue();
        int runningSum = 0;

        if (addImmediate) {
            int n = memoryManager.readMemory(register.pc.getValue());
            register.pc.inc();
            runningSum = n;
        } else {
            runningSum = value;
        }

        if (addCarry) {
            if (register.isC()) {
                runningSum++;
            }
        }

        reg1.add(runningSum);

        /* Set flags */
        register.clearAllFlags();

        register.setZ(reg1.getValue() == 0);

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

        return reg1.getValue();
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

    private int cpu8BitSub(UInt reg1, UInt reg2, boolean useImmediate, boolean subCarry) {
       return cpu8BitSub(reg1, reg2.getValue(), useImmediate, subCarry); 
    }

    /*
     * TODO: Perhaps I don't need to return a value, since reg is of type
     * UInt and is passed by reference. Have another look at this later.
     */
    private int cpu8BitSub(UInt reg, int value, boolean useImmediate, boolean subCarry) {
        int initialValue = reg.getValue();
        UInt runningDifference = new UInt(UInt.EIGHT_BITS);

        if (useImmediate) {
            int n = memoryManager.readMemory(register.pc.getValue());
            register.pc.inc();
            runningDifference.setValue(n);
        } else {
            runningDifference.setValue(value);
        }

        if (subCarry) {
            if (register.isC()) {
                runningDifference.inc();
            }
        }

        reg.sub(runningDifference.getValue());

        /* now set flags */
        register.clearAllFlags();

        register.setZ(reg.getValue() == 0);

        register.setN();

        if (initialValue < runningDifference.getValue()) {
            register.setC();
        }

        int halfCarry = initialValue & 0xF;
        halfCarry = halfCarry - (runningDifference.getValue() & 0xF);

        if (halfCarry < 0) {
            register.setH();
        }

        return reg.getValue();
    }

    private void cpu8BitSPAdd() {
        int n = memoryManager.readMemory(register.pc.getValue());
        int result = (register.pc.getValue() + n) & 0xFFFF;
        register.pc.setValue(result);

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

        register.setC(((register.pc.getValue() ^ n ^ result) & 0x100) != 0);
        register.setH(((register.pc.getValue() ^ n ^ result) & 0x10) != 0);
    }

    private int cpu8BitXOR(UInt reg1, UInt reg2, boolean useImmediate) {
        return cpu8BitXOR(reg1, reg2.getValue(), useImmediate);
    }

    private int cpu8BitXOR(UInt reg, int value, boolean useImmediate) {

        int n = 0;

        if (useImmediate) {
            n = memoryManager.readMemory(register.pc.getValue());
            register.pc.inc();
        } else {
            n = value;
        }

        reg.setValue(reg.getValue() ^ n);

        register.clearAllFlags();

        register.setZ(reg.getValue() == 0);

        return reg.getValue();
    }
}
