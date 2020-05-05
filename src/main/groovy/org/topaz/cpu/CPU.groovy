package org.topaz.cpu

import org.topaz.cpu.Register
import org.topaz.InterruptHandler
import org.topaz.MemoryManager

class CPU{
    Register register
    MemoryManager memoryManager

    public CPU(MemoryManager memoryManager) {
        this.register = new Register()    
        this.memoryManager = memoryManager
    }
}