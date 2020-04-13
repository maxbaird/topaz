package org.topaz

import Cartridge
import Register

class Topaz{
  private static final int ROM_SIZE = 0x2000000

	 public static void main(args) {

  def f = new File("/home/maxx/Documents/alba.pdf")
	 def cartridge = new Cartridge(f, ROM_SIZE)
  println cartridge.load().length

  def reg = new Register()

  reg.setZ(true)
  println reg.printFlags()

	}
}
