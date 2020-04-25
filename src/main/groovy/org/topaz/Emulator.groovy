package org.topaz

import org.topaz.Cartridge
import org.topaz.cpu.Register
import org.topaz.cpu.CPU

class Emulator{
    private static final int ROM_SIZE = 0x2000000
    private static final MAX_CYCLES = 69905

    CPU cpu

    public Emulator(Cartridge cartridge){
        this.cpu = new CPU()
    }
    
    public void Update() {
        int cyclesThisUpdate = 0    
        
        while(cyclesThisUpdate < MAX_CYCLES) {
            int cycles = this.executeNextOpCode()
            cyclesThisUpdate += cycles
            this.updateTimers(cycles)
            this.updateGraphics(cycles)
            this.doInterrupts()
        }
        this.renderScreen()
    }
    
    private int executeNextOpCode() {
        return 1
    }
    
    private void updateTimers(int cycles) {
        
    }
    
    private void updateGraphics(int cycles) {
        
    }
    
    private void doInterrupts(int cycles) {
        
    }
    
    private void renderScreen() {
        
    }
}