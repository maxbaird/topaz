package org.topaz

import org.topaz.Cartridge
import org.topaz.cpu.Register
import org.topaz.cpu.CPU
import org.topaz.MemoryManager
import org.topaz.Timer

class Emulator{
    private static final MAX_CYCLES = 69905

    CPU cpu
    MemoryManager memoryManager
    Cartridge cartridge
    Timer timer

    public Emulator(Cartridge cartridge){
        this.cpu = new CPU()
        this.cartridge = cartridge
        this.memoryManager = new MemoryManager(this.cartridge)
        this.timer = new Timer(memoryManager: this.memoryManager)
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