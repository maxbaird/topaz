package org.topaz.cpu

import org.topaz.cpu.Register
import org.topaz.cpu.MemoryManager

class CPU{
    Register register
    MemoryManager memoryManager
    
    public CPU() {
        this.register = new Register()    
        this.memoryManager = new MemoryManager()
    }
}