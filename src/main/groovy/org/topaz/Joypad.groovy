package org.topaz

import org.topaz.util.BitUtil
import org.topaz.MemoryManager

class Joypad{
    public static final int KEY_REGISTER = 0xFF00
    
    /*
     * VBA emulator defaults the A button to Z
     * and the B button to X.
     */
//    public static final int A_KEY = 4
//    public static final int B_KEY = 5
//    public static final int START_KEY = 7
//    public static final int SELECT_KEY = 6
//    public static final int RIGHT_KEY = 0
//    public static final int LEFT_KEY = 1
//    public static final int UP_KEY = 2
//    public static final int DOWN_KEY = 3
    
    private int joypadState
   
    int getJoypadState() {
        return 0
    } 
    
    private void keyPressed(int key) {
       boolean  previouslyUnset = false
       
       /*
        * If setting the state of this key from 1 to 0 (where 0 means a press)
        * an interrupt request may be necessary.
        */
        if(BitUtil.isSet(joypadState, key) == false) {
            previouslyUnset = true
        }
        
        /*
         * For the joypad, a keypress is represented by 0
         * and not 1.
         */
        joypadState = BitUtil.clearBit(joypadState, key)
        
        boolean standardButtonPressed = true
        
        /*
         * Determine whether a standard or directional button has been pressed
         * TODO Might have to rework this logic based on how keypresses work in java
         */
        if(key > 3) {
           standardButtonPressed = true 
        }else {
            standardButtonPressed = false
        }
        
        int keyRequest = MemoryManager.rom[KEY_REGISTER]
        boolean requestInterrupt = false
    }
}