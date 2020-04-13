package org.topaz

import Cartridge
import Register

class Topaz{
	static main(args) {
	 def cartridge = Cartridge.load("/home/maxx/Documents/alba.pdf")
  println cartridge.length
  def reg = new Register()

  reg.setZ(true)
  println reg.printFlags()
	}
}
