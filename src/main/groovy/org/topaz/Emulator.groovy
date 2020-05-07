package org.topaz

import org.topaz.Cartridge
import org.topaz.cpu.Register
import org.topaz.cpu.CPU
import org.topaz.MemoryManager
import org.topaz.Timer
import org.topaz.InterruptHandler
import org.topaz.lcd.LCD

class Emulator{
    private static final MAX_CYCLES = 69905

    CPU cpu
    Register register
    MemoryManager memoryManager
    Cartridge cartridge
    Timer timer
    InterruptHandler interruptHandler
    LCD lcd

    public Emulator(Cartridge cartridge){
        this.cartridge = cartridge
        this.register = new Register()
        this.memoryManager = new MemoryManager(this.cartridge, this.register)
        this.cpu = new CPU(memoryManager: this.memoryManager, register:this.register)
        this.interruptHandler = new InterruptHandler(memoryManager:this.memoryManager, cpu:this.cpu)
        this.timer = new Timer(memoryManager: this.memoryManager, interruptHandler: this.interruptHandler)
        this.lcd = new LCD(memoryManager: this.memoryManager, interruptHandler:this.interruptHandler)
    }
    
    public void update() {
        int cyclesThisUpdate = 0    
        
        while(cyclesThisUpdate < MAX_CYCLES) {
            int cycles = this.executeNextOpCode()
            cyclesThisUpdate += cycles
            this.updateTimers(cycles)
            this.updateGraphics(cycles)
            this.handleInterrupts()
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
       lcd.updateGraphics(cycles) 
    }
    
    private void handleInterrupts() {
       interruptHandler.handleInterrupts() 
    }
    
    private void renderScreen() {
        
    }
}