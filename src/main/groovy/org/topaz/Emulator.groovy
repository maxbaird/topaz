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
import org.topaz.debug.StateDumper

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
    StateDumper dumper

    public Emulator(Cartridge cartridge){
        this.cartridge = cartridge
        this.register = new Register()
        this.joypad = new Joypad()
        this.memoryManager = new MemoryManager(this.cartridge, this.register, this.joypad)
        this.dumper = new StateDumper(this.memoryManager, this.register)
        this.cpu = new CPU(memoryManager: this.memoryManager, register:this.register, dumper:this.dumper)
        this.interruptHandler = new InterruptHandler(memoryManager:this.memoryManager, cpu:this.cpu)
        this.timer = new Timer(memoryManager: this.memoryManager, interruptHandler: this.interruptHandler)
        joypad.interruptHandler = interruptHandler
        //TODO Pass an instance of the screen for the GPU to update
        this.display = new Display()
        this.gpu = new GPU(this.memoryManager, this.interruptHandler, this.display)
    }

    public void start() {
        println 'start'
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

        def n = 0
        while(cyclesThisUpdate < MAX_CYCLES) {
            n++
            if(n == 250) {
                println 'exiting'
                System.exit(-1)
            }
            int cycles = 0
            cycles = this.executeNextOpCode(n)
            cyclesThisUpdate += cycles
            this.updateTimers(cycles)
            this.updateGraphics(cycles)
            this.handleInterrupts()
        }
        this.renderScreen()
    }

    private int executeNextOpCode(int n) {
        if(!cpu.isHalted) {
            return cpu.executeNextOpcode(n)
        }else {
            /*
             * 4 is the number of cycles the opcode for halting the CPU takes.
             */
            return 4    
        }
        
        /*
         * Interrupts are only enabled or disabled after the next instruction.
         * Opcode 0xF3 disables interrupt and opcode 0xFB enables them. So if
         * the previous instruction was neither the opcode for enabling or
         * disabling interrupts, the boolean values are reset.
         */
        if(cpu.interruptsDisabled) {
            if(memoryManager.readMemory(register.pc - 1) != 0xF3) {
                cpu.interruptsDisabled = false
                interruptHandler.interruptsEnabled = false
            }
        }
        
        if(cpu.interruptsEnabled) {
            if(memoryManager.readMemory(register.pc - 1) != 0xFB) {
                cpu.interruptsEnabled = false
                interruptHandler.interruptsEnabled = true
            }
        }
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