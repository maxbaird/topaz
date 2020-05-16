package org.topaz

import org.topaz.util.BitUtil
import org.topaz.MemoryManager
import org.topaz.InterruptHandler

class Joypad{
    /*
     * The joypad consists of 8 buttons, 4 directional buttons (up, down, left,
     * right) and standard buttons (start, select, A, B). The joypad register is
     * at address 0xFF00 and its bits are used as follows:
     * 
     * Bit 7 - Not used
     * Bit 6 - Not used
     * Bit 5 - P15 Select Button Keys      (0=Select)
     * Bit 4 - P14 Select Direction Keys   (0=Select)
     * Bit 3 - P13 Input Down  or Start    (0=Pressed) (Read Only)
     * Bit 2 - P12 Input Up    or Select   (0=Pressed) (Read Only)
     * Bit 1 - P11 Input Left  or Button B (0=Pressed) (Read Only)
     * Bit 0 - P10 Input Right or Button A (0=Pressed) (Read Only)
     * 
     * Bits 0 to 3 are set by the emulator so the game can know the joypad's
     * state. The directional buttons and standard buttons both share the same
     * range of bits. The game must sit bit 4 or 5 depending on which set of
     * button inputs it is interested in.
     */
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

    /*
     * 
     */
    private def keyRegisterBit = [
        DIRECTIONAL_BUTTON: 4,
        STANDARD_BUTTON:5
    ].asUnmodifiable()

    private int joypadState
    private InterruptHandler interruptHandler

    int getJoypadState() {
        int keyRegister = MemoryManager.rom[KEY_REGISTER]
        
        /*
         * Flip all bits so that 0 becomes 1 and 1 becomes zero so that we can
         * make use of the methods to check for a set bit. With the joypad, a
         * keypress is represented by 0, if we flip the bits, we can detect
         * keypresses as 1.
         */
        keyRegister = keyRegister ^ 0xFF
        
        if(BitUtil.isSet(keyRegister, keyRegisterBit.DIRECTIONAL_BUTTON)) {
            int joypad = joypadState >> 4
            joypad
        }
    }

    public void keyPressed(int key) {
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

        /*
         *  The memory manager's read memory method is unused since reading from
         *  this location needs to get the Joypad's state. Which will ultimately
         *  call this method again resulting in infinite recursion.
         */
        int keyRequest = MemoryManager.rom[KEY_REGISTER]
        boolean requestInterrupt = false
        
        /*
         * Request interrupt if the button just pressed is the type of button
         * the game is interested in.
         */
        if(standardButtonPressed && !BitUtil.isSet(keyRequest, keyRegisterBit.STANDARD_BUTTON)) {
            requestInterrupt = true
        }else if(!standardButtonPressed && !BitUtil.isSet(keyRequest, keyRegisterBit.DIRECTIONAL_BUTTON)) {
           requestInterrupt = true 
        }
        
        if(requestInterrupt && !previouslyUnset) {
           interruptHandler.requestInterrupt(InterruptHandler.JOYPAD_INTERRUPT) 
        }
    }
    
    public void keyReleased(int key) {
        joypadState = BitUtil.setBit(joypadState, key)
    }
}