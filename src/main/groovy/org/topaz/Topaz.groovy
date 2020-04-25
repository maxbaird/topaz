package org.topaz

import org.topaz.Cartridge
import org.topaz.cpu.Register
import org.topaz.Emulator

class Topaz{
    Emulator emulator

    public static void main(String []args) {
        new Topaz()
        println 'done...'
    }

    public Topaz(){
        //TODO create GUI and get file
        def f = new File("/home/maxx/Documents/Games/bandicoot.GBA")
        def cartridge = new Cartridge(f)
        emulator = new Emulator(cartridge)
    }
}
