package org.topaz.debug

import org.topaz.cpu.Register2
import org.topaz.MemoryManager

class StateDumper{
    Register2 register
    MemoryManager memoryManager
    
    StringBuilder sb
    
    public StateDumper(MemoryManager memoryManager, Register2 register){
        this.memoryManager = memoryManager
        this.register = register
        
        sb = new StringBuilder(memoryManager.rom.length * 3)
    }
    
    def dump(def iteration, def opcode, def extendedOpcode, def cycles, def fileName){
        println 'Dumping state: ' + fileName
        sb.length = 0
        sb.append("Iteration: " + iteration + '\n')
        sb.append("Opcode: " + opcode + '\n')
        sb.append("Extended Opcode: " + extendedOpcode + '\n')
        sb.append("Cycles: " + cycles + '\n')
        sb.append('===========\n')
        sb.append(register.toString())
        sb.append('===========\n')
        
        memoryManager.rom.eachWithIndex{it, idx->
            sb.append(String.format("0x%02X: %d\n", idx, it))
        }
        
        new File(fileName).newWriter().withWriter {w->
            w << sb
        }
        
        println 'Dumped state: ' + fileName
    }
}