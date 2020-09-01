package org.topaz

import org.topaz.Cartridge
import org.topaz.cpu.Register
import org.topaz.Emulator

class Topaz{
	Emulator emulator
	public static int executionStart
	public static int executionEnd
	public static int executionLimit

	public static void main(String []args) {
		def val = 0

		if(args.length != 0 ) {
			try {
				val = args[0] as int
			}catch(Exception e) {
				println 'Error converting ' + args[0] + ':' + e.message
				System.exit(-1)
			}
		}

		executionStart = val
		executionEnd = val + 500
		executionLimit = executionEnd + 500

		def str = "Execution Start: " + executionStart + "\n" +
				"Execution End: " + executionEnd + "\n" +
				"Executin Limit: " + executionLimit + "\n"

		if(args.length >= 2) {
			new Topaz(args[1])
		}else {
			new Topaz(null)
		}
	}

	public Topaz(def path){
        String rom = "../cpu_instrs/individual/01-special.gb"
        //String rom = "/home/maxx/Documents/Games/terminator2.gb"

		def f = (path != null) ? new File(path) : new File(rom)
		//def f = new File("/home/maxx/Documents/Games/terminator2.gb")
		//def f = new File("/home/maxx/Downloads/dmg0_rom.bin")
		def cartridge = new Cartridge(f)
		emulator = new Emulator(cartridge)
		emulator.start()
	}
}
