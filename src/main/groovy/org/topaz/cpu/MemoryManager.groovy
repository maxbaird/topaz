package org.topaz.cpu

import org.topaz.Cartridge
import org.topaz.util.BitUtil

class MemoryManager{
    private static final int MEMORY_SIZE = 0x10000
    private int []rom

    Cartridge cartridge

    public MemoryManager(Cartridge cartridge) {
        this.cartridge = cartridge
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

    int readMemory(final int address) {
        /* Reading from ROM bank */
        if((address >= 0x4000) && (address <= 0x7FFF)) {
            int newAddress = address - 0x4000
            return this.cartridge.memory[newAddress + (this.cartridge.currentRomBank * 0x4000)]
        }

        /* Reading from RAM memory bank */
        else if((address >= 0xA000) && (address <= 0xBFFF)) {
            int newAddress = address - 0xA000
            return this.cartridge.ramBanks[newAddress + (this.cartridge.currentRamBank * 0x2000)]
        }

        return this.rom[address]
    }

    void writeMemory(int address, int data) {
        /* Read only cartridge memory, no *normal* writes allowed */
        if(address < 0x8000) {
            /*
             * The only writes allowed here are to change the MBC controllers
             * writing to memory lower than 0x8000 means attempting to write
             * into the cartridge ROM. This should only be done to change the
             * current ROM or RAM bank.
             */
            this.handleBanking(address, data)
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

    void handleBanking(int address, int data) {
        /*
         * If the data to be written occurs between 0x0 to 0x2000, this
         * indicates that RAM banking should be enabled
         */
        if(address < 0x2000) {
            if(this.cartridge.isMBC1 || this.cartridge.isMBC2) {
                this.doRamBankEnable(address, data)
            }
        }

        /*
         * If the data is being written in the range 0x2000 to 0x4000 then
         * writing to this address space sets the lower 5 bits of the current
         * ROM bank.
         */
        else if((address >= 0x2000) || (address < 0x4000)) {
            if(this.cartridge.isMBC1 || this.cartridge.isMBC2) {
                this.doChangeLoROMBank(data)
            }
        }
        /*
         * If the data is being written between 0x4000 and 0x6000 then it's
         * either a ROM or RAM bank change depending on the currently selected
         * ROM/RAM mode.
         */
        else if((address >= 0x4000) || (address < 0x6000)) {
            /*
             * MBC2 has no RAM bank, so only deal with MBC1
             */
            if(this.cartridge.isMBC1) {
                if(this.cartridge.isRomBanking) {
                    this.doChangeHiROMBank(data)
                }else {
                    this.doRAMBankChange(data)
                }
            }
        }
        /*
         * This is a 1 bit register that indicates what should happen
         * when the game writes to the address range 0x4000 to 0x6000 (above)
         */
        else if((address >= 0x6000) || (address < 0x8000)) {
            if(this.cartridge.isMBC1) {
                this.doChangeRomRamMode(data)
            }
        }
    }

    private void doRamBankEnable(int address, int data) {
        /*
         * Before writing to any RAM bank the game must first enable RAM bank
         * writing. This is done by attempting to write to cartridge ROM in the
         * address range 0x0 to 0x2000. For MBC1 if the lower nibble of the data
         * being written is 0xA then RAM bank writing is enabled. If the lower
         * nibble is 0x0 then it is disabled. The same is true for MBC2 except
         * that bit 4 of the address byte must be 0.
         */
        if(this.cartridge.isMBC2) {
            if(BitUtil.testBit(address, 4)) {
                return
            }
        }
        
        int lowerNibble = data & 0xF
        
        if(lowerNibble == 0xA) {
            this.cartridge.enableRam = true
        }else if(lowerNibble == 0x0) {
            this.cartridge.enableRam = false
        }
    }

    private void doChangeLoROMBank(int data) {
        /*
         * If MBC1 is being used, the current ROM bank can be changed in two
         * ways. The game can can write to memory address 0x2000 - 0x3FFF where
         * it only changes the lower 5 bits of the current ROM bank or it can
         * write to address 0x4000 - 0x5FFF during ROM banking mode where only
         * bits 5 and 6 are changed. However, if MBC2 is being used, the game
         * also writes to 0x2000 - 0x3FFF, but only the lower 3 bits are
         * changed.
         * 
         * Regardless, of which MBC is used, if its current value is 0 it must
         * be set to 1. The reason for this is that ROM bank 0 is static and
         * always accessible in the range 0x000 - 0x4000 and so ROM bank 0
         * should never be loaded into this space. If set to 0 it will be
         * treated as ROM bank 1.
         */
        if(this.cartridge.isMBC2) {
            this.cartridge.currentRomBank = data & 0xF
            if(this.cartridge.currentRomBank == 0) {
                this.cartridge.currentRomBank = 1
            }
            return
        }
        
        /*
         * In the case of MBC1, the lower 5 bits of the current ROM bank must be
         * set to the lower 5 bits of data.
         */
        int lower5 = data & 31 /* Get lower 5 bits from data */
        this.cartridge.currentRomBank &= 224 /* Clear lower 5 bits of current ROM bank */
        this.cartridge.currentRomBank |= lower5
        
        if(this.cartridge.currentRomBank == 0) {
            this.cartridge.currentRomBank = 1
        }
    }

    private void doChangeHiROMBank(int data) {
        /*
         * This is the second way of changing the current ROM bank in MBC1 mode.
         * Here, bits 5 and 6 are changed when writing to memory address 0x4000
         * to 0x6000.
         */
        data &= 244 /* Set lower 5 bits to 0 */
        this.cartridge.currentRomBank |= data
        
        if(this.cartridge.currentRomBank == 0) {
            this.cartridge.currentRomBank = 1
        }
    }

    private void doRAMBankChange(data) {
        /*
         * This should only be done in MCB1. 
         */
        this.cartridge.currentRamBank = data & 0x3
    }

    private void doChangeRomRamMode(int data) {
        /*
         * This method is responsible for selecting the current banking mode,
         * either ROM or RAM. When data is written to memory address 0x4000 to
         * 0x6000, if the data's LSB is 0 then ROM banking is the selected mode.
         * Otherwise RAM banking is done. The current RAM bank is set to 0
         * whenever ROM banking is enabled because only RAM bank 0 is useable in
         * this mode.
         */
        int newData = data & 0x1
        this.cartridge.isRomBanking = (newData == 0) ? true : false
        
        if(this.cartridge.isRomBanking) {
            this.cartridge.currentRamBank = 0
        }
    }
}