package org.topaz

import org.topaz.Cartridge
import org.topaz.cpu.Register
import org.topaz.Emulator

class Topaz{
    Emulator emulator
    static int executionStart
    static int executionEnd
    static int executionLimit

    public static void main(String []args) {
        def val
        try {
            val = args[0] as int
        }catch(Exception e) {
            println 'Error converting ' + args[0] + ':' + e.message
            System.exit(-1)
        }

        executionStart = val
        executionEnd = val + 500
        executionLimit = executionEnd + 500

        def str = "Execution Start: " + executionStart + "\n" +
                "Execution End: " + executionEnd + "\n" +
                "Executin Limit: " + executionLimit + "\n"

        println str
        
        new Topaz()
    }

    public Topaz(){
        def f = new File("/home/maxx/Documents/Games/terminator2.gb")
        //def f = new File("/home/maxx/Downloads/dmg0_rom.bin")
        def cartridge = new Cartridge(f)
        emulator = new Emulator(cartridge)
        emulator.start()
    }
}
