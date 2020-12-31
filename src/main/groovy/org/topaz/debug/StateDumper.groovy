package org.topaz.debug

import org.topaz.cpu.Register2
import org.topaz.MemoryManager
import org.topaz.cpu.CPU2

class StateDumper{
    Register2 register
    MemoryManager memoryManager
	CPU2 cpu
    
    StringBuilder sb
    
    public StateDumper(MemoryManager memoryManager, Register2 register){
        this.memoryManager = memoryManager
        this.register = register
        
        sb = new StringBuilder(memoryManager.rom.length * 3)
    }
	
	private def buildInterruptState() {
		def str = 'PendingInterruptEnabled: ' + cpu.pendingInterruptEnabled +
			   '\nInterruptMaster: ' + cpu.interruptMaster +
			   '\nHalt: ' + cpu.isHalted +
			   '\n===========\n'
		
		return str
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
		sb.append(this.buildInterruptState())
        
        memoryManager.rom.eachWithIndex{it, idx->
            sb.append(String.format("0x%02X: %d\n", idx, it))
        }
        
        new File(fileName).newWriter().withWriter {w->
            w << sb
        }
        
        println 'Dumped state: ' + fileName
    }
}