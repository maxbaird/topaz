package org.topaz.debug

public class Debug{

	public static int instructionCounter = 0

	public static print(String msg, int instructionNo, boolean terminate) {
		if(instructionNo == this.instructionCounter) {
			println '[' + instructionNo + ']: ' + msg

			if(terminate) {
				System.err.println("Exiting from Debug print")
				System.exit(0)
			}
		}
	}
}