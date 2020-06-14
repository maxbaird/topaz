package org.topaz

import org.topaz.Cartridge
import org.topaz.cpu.Register2
import org.topaz.gpu.GPU2
import org.topaz.cpu.CPU2
import org.topaz.MemoryManager
import org.topaz.Timer
import org.topaz.InterruptHandler
import org.topaz.Joypad
import org.topaz.ui.Display
import org.topaz.debug.StateDumper
import org.topaz.Topaz

class Emulator{
    private static final MAX_CYCLES = 69905

    CPU2 cpu
    GPU2 gpu
    Register2 register
    MemoryManager memoryManager
    Cartridge cartridge
    Timer timer
    InterruptHandler interruptHandler
    Joypad joypad
    Display display
    StateDumper dumper

    public Emulator(Cartridge cartridge){
        this.cartridge = cartridge
        this.register = new Register2()
        this.joypad = new Joypad()
        this.memoryManager = new MemoryManager(this.cartridge, this.register, this.joypad)
        this.dumper = new StateDumper(this.memoryManager, this.register)
        this.cpu = new CPU2(this.memoryManager, this.register, this.dumper)
        this.interruptHandler = new InterruptHandler(memoryManager:this.memoryManager, cpu:this.cpu)
        this.timer = new Timer(memoryManager: this.memoryManager, interruptHandler: this.interruptHandler)
        joypad.interruptHandler = interruptHandler
        //TODO Pass an instance of the screen for the GPU to update
        this.display = new Display(joypad)
        this.gpu = new GPU2(this.memoryManager, this.interruptHandler, this.display)
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

    def n = 0
    public void update() {
        //println 'Updating game...'
        int cyclesThisUpdate = 0

        while(cyclesThisUpdate < MAX_CYCLES) {
            n++
            if ((n%100000) == 0) {
                println n
            }
//            if(n == Topaz.executionLimit) {
            if(n == 330000) {
                println 'exiting at : ' + n
                println 'Press enter to continue...'
                System.in.newReader().readLine()
                //System.exit(-1)
            }
            int cycles = 0
            cycles = this.executeNextOpCode(n)
            cyclesThisUpdate += cycles
            this.updateTimers(cycles)
            this.updateGraphics(cycles)
            cyclesThisUpdate += this.handleInterrupts()
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

        //        /*
        //         * Interrupts are only enabled or disabled after the next instruction.
        //         * Opcode 0xF3 disables interrupt and opcode 0xFB enables them. So if
        //         * the previous instruction was neither the opcode for enabling or
        //         * disabling interrupts, the boolean values are reset.
        //         */
        //        if(cpu.pendingInterruptDisabled) {
        //            if(memoryManager.readMemory(register.pc - 1) != 0xF3) {
        //                cpu.pendingInterruptDisabled = false
        //                interruptHandler.interruptsEnabled = false
        //            }
        //        }
        //
        //        if(cpu.pendingInterruptEnabled) {
        //            if(memoryManager.readMemory(register.pc - 1) != 0xFB) {
        //                cpu.pendingInterruptEnabled = false
        //                interruptHandler.interruptsEnabled = true
        //            }
        //        }
    }

    private void updateTimers(int cycles) {
        timer.updateTimer(cycles)
    }

    private void updateGraphics(int cycles) {
        gpu.updateGraphics(cycles)
    }

    private int handleInterrupts() {
        /*
         *  If the EI (enable interrupt) instruction was executed the interrupt
         *  master is set as enabled for the next instruction cycle.
         */
        if (cpu.pendingInterruptEnabled) {
            cpu.pendingInterruptEnabled = false
            cpu.interruptMaster = true
            return 0
        }

        /*
         * If the CPU is neither interrupted or halted, there is no need to
         * proceed with handling interrupts.
         */
        if(!cpu.interruptMaster && !cpu.isHalted) {
            return 0
        }

        if(cpu.interruptMaster || cpu.isHalted) {
            interruptHandler.handleInterrupts()
            return 20
        }

        return 0
    }

    private void renderScreen() {
        gpu.updateDisplay()
    }
}
