package org.topaz

import org.topaz.Cartridge
import org.topaz.cpu.Register
import org.topaz.cpu.CPU
import org.topaz.MemoryManager
import org.topaz.Timer
import org.topaz.InterruptHandler

class Emulator{
    private static final MAX_CYCLES = 69905

    CPU cpu
    MemoryManager memoryManager
    Cartridge cartridge
    Timer timer
    InterruptHandler interruptHandler

    public Emulator(Cartridge cartridge){
        this.cartridge = cartridge
        this.memoryManager = new MemoryManager(this.cartridge)
        this.cpu = new CPU(this.memoryManager)
        this.timer = new Timer(memoryManager: this.memoryManager, cpu: this.cpu)
        this.interruptHandler = new InterruptHandler(memoryManager:this.memoryManager, cpu:this.cpu)
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
       timer.updateTimer(cycles) 
    }
    
    private void updateGraphics(int cycles) {
        
    }
    
    private void doInterrupts() {
       interruptHandler.handleInterrupts() 
    }
    
    private void renderScreen() {
        
    }
}