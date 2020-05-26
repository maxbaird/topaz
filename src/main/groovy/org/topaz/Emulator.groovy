package org.topaz

import org.topaz.Cartridge
import org.topaz.cpu.Register
import org.topaz.gpu.GPU
import org.topaz.cpu.CPU
import org.topaz.MemoryManager
import org.topaz.Timer
import org.topaz.InterruptHandler
import org.topaz.Joypad
import org.topaz.ui.Display

class Emulator{
    private static final MAX_CYCLES = 69905

    CPU cpu
    GPU gpu
    Register register
    MemoryManager memoryManager
    Cartridge cartridge
    Timer timer
    InterruptHandler interruptHandler
    Joypad joypad
    Display display

    public Emulator(Cartridge cartridge){
        this.cartridge = cartridge
        this.register = new Register()
        this.joypad = new Joypad()
        this.memoryManager = new MemoryManager(this.cartridge, this.register, this.joypad)
        this.cpu = new CPU(memoryManager: this.memoryManager, register:this.register)
        this.interruptHandler = new InterruptHandler(memoryManager:this.memoryManager, cpu:this.cpu)
        this.timer = new Timer(memoryManager: this.memoryManager, interruptHandler: this.interruptHandler)
        joypad.interruptHandler = interruptHandler
        //TODO Pass an instance of the screen for the GPU to update
        this.display = new Display()
        this.gpu = new GPU(this.memoryManager, this.interruptHandler, this.display)
    }

    public void start() {
        Thread thread = new Thread(new Runnable() {
                    //double interpolation = 0
                    final int TICKS_PER_SECOND = 60
                    final int SKIP_TICKS = 1000 / TICKS_PER_SECOND
                    final int MAX_FRAMESKIP = 5
                    @Override
                    public void run() {
                        double next_game_tick = System.currentTimeMillis()
                        int loops

                        while (true) {
                            loops = 0
                            while (System.currentTimeMillis() > next_game_tick && loops < MAX_FRAMESKIP) {

                                update()

                                next_game_tick += SKIP_TICKS
                                loops++
                            }

                            //interpolation = (System.currentTimeMillis() + SKIP_TICKS - next_game_tick
                            //                                            / (double) SKIP_TICKS)

                        }
                    }
                })
        thread.start()
    }

    public void update() {
        println 'Updating game...'
        int cyclesThisUpdate = 0

        while(cyclesThisUpdate < MAX_CYCLES) {
            int cycles = 4
            if(!cpu.isHalted) {
                cycles = this.executeNextOpCode()
            }

            cyclesThisUpdate += cycles
            this.updateTimers(cycles)
            this.updateGraphics(cycles)
            this.handleInterrupts()
        }
        this.renderScreen()
    }

    private int executeNextOpCode() {
        return cpu.executeNextOpcode()
    }

    private void updateTimers(int cycles) {
        timer.updateTimer(cycles)
    }

    private void updateGraphics(int cycles) {
        gpu.updateGraphics(cycles)
    }

    private void handleInterrupts() {
        interruptHandler.handleInterrupts()
    }

    private void renderScreen() {
        gpu.updateDisplay()
    }
}