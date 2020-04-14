package org.topaz

class Cartridge{
		private static final int MAX_ROM_SIZE = 0x2000000

		private File rom
		int [] memory

		public Cartridge(File rom){
			 memory = new int[rom.bytes.length]
				this.rom = rom
				this.load()
		}

		private void load(){
		 	this.rom.bytes.eachWithIndex{it, idx->
				this.memory[idx] = it & 0xFF
				}
	 }
}

