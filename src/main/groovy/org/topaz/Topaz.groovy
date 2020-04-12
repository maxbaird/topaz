package org.topaz

import Cartridge

class Topaz{
	static main(args) {
	 def cartridge = Cartridge.load("/home/maxx/Documents/alba.pdf")
  println cartridge.length
	}
}
