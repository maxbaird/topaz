package org.topaz

class Cartridge{
    private static final int MAX_ROM_SIZE = 0x2000000
    private static final int RAM_BANK_SIZE = 0x8000
    private File rom

    boolean isMBC1 = false
    boolean isMBC2 = false
    int currentRomBank = 1
    boolean enableRam = false
    boolean isRomBanking = true

    int []ramBanks
    int currentRamBank = 0

    int [] memory

    public Cartridge(File rom){
        memory = new int[MAX_ROM_SIZE]
        this.rom = rom
        this.ramBanks = new int[RAM_BANK_SIZE]
        this.load()
        this.setMBC()
    }

    private void load(){
        this.rom.bytes.eachWithIndex{it, idx->
            this.memory[idx] = it & 0xFF
        }
    }

    private void setMBC() {
        switch(this.memory[0x147]) {
            case 1 : this.isMBC1 = true; break
            case 2 : this.isMBC1 = true; break
            case 3 : this.isMBC1 = true; break
            case 5 : this.isMBC2 = true; break
            case 6 : this.isMBC2 = true; break
            default : break
        }
    }
}