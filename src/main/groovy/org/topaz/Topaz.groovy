package org.topaz

import org.topaz.Cartridge
import org.topaz.cpu.Register
import org.topaz.Emulator

class Topaz{
    Emulator emulator

    public static void main(String []args) {
        new Topaz()
    }

    public Topaz(){
        def f = new File("/home/maxx/Documents/Games/terminator2.gb")
        def cartridge = new Cartridge(f)
        emulator = new Emulator(cartridge)
        emulator.start()
    }
}
