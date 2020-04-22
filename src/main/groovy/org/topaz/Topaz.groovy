package org.topaz

import org.topaz.Cartridge
import org.topaz.Register

class Topaz{
    private static final int ROM_SIZE = 0x2000000

    public static void main(String []args) {
        def f = new File("/home/maxx/Documents/Games/bandicoot.GBA")
        def cartridge = new Cartridge(f)
        println cartridge.memory.length

        def reg = new Register()

        reg.setZ(true)
        println reg.printFlags()
    }

    public Topaz(){
    }
}
