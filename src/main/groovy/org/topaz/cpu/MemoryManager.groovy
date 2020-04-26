package org.topaz.cpu

class MemoryManager{
    private static final int MEMORY_SIZE = 0x10000
    private int []rom

    public MemoryManager() {
        this.rom = new int[MEMORY_SIZE]
        this.init()
    }

    private void init() {
        this.rom[0xFF05] = 0x00
        this.rom[0xFF06] = 0x00
        this.rom[0xFF07] = 0x00
        this.rom[0xFF10] = 0x80
        this.rom[0xFF11] = 0xBF
        this.rom[0xFF12] = 0xF3
        this.rom[0xFF14] = 0xBF
        this.rom[0xFF16] = 0x3F
        this.rom[0xFF17] = 0x00
        this.rom[0xFF19] = 0xBF
        this.rom[0xFF1A] = 0x7F
        this.rom[0xFF1B] = 0xFF
        this.rom[0xFF1C] = 0x9F
        this.rom[0xFF1E] = 0xBF
        this.rom[0xFF20] = 0xFF
        this.rom[0xFF21] = 0x00
        this.rom[0xFF22] = 0x00
        this.rom[0xFF23] = 0xBF
        this.rom[0xFF24] = 0x77
        this.rom[0xFF25] = 0xF3
        this.rom[0xFF26] = 0xF1
        this.rom[0xFF40] = 0x91
        this.rom[0xFF42] = 0x00
        this.rom[0xFF43] = 0x00
        this.rom[0xFF45] = 0x00
        this.rom[0xFF47] = 0xFC
        this.rom[0xFF48] = 0xFF
        this.rom[0xFF49] = 0xFF
        this.rom[0xFF4A] = 0x00
        this.rom[0xFF4B] = 0x00
        this.rom[0xFFFF] = 0x00
    }
    
    void writeMemory(int address, int data) {
        /* Read only memory, no writes allowed */
        if(address < 0x800) {
            return
        }
        /* Writing to ECHO ram also writes to RAM */ 
        else if((address >= 0xE000) && (address < 0xFE00)) {
            this.rom[address] = data
            this.writeMemory(address-0x2000, data)
        }
        /* Unused memory, no writes should happen here */
        else if((address >= 0xFEA0) && (address < 0xFEFF)) {
            return
        }else {
            this.rom[address] = data
        }
    }
}