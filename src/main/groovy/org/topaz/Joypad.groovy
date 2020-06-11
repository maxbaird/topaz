package org.topaz

import org.topaz.util.BitUtil
import org.topaz.MemoryManager
import org.topaz.InterruptHandler
import java.awt.event.KeyEvent

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

    def inputMap =[(KeyEvent.VK_Z):4,
        (KeyEvent.VK_X):5,
        (KeyEvent.VK_ENTER):7,
        (KeyEvent.VK_BACK_SPACE):6,
        (KeyEvent.VK_RIGHT):0,
        (KeyEvent.VK_LEFT):1,
        (KeyEvent.VK_UP):2,
        (KeyEvent.VK_DOWN):3
    ].asUnmodifiable()
    
    /*
     * 
     */
    private def keyRegisterBit = [
        DIRECTIONAL_BUTTON: 4,
        STANDARD_BUTTON:5
    ].asUnmodifiable()

    /*
     * Although the game expects the status of the 8 buttons to be stored within
     * bits 0 to 3 of address 0xFF00 (KEY_REGISTER), the Joypad state variable
     * uses each bit to represent each button as there are 8 bits, one for each
     * button (it matches neatly). When the game queries the KEY_REGISTER,
     * getJoypadState() is called to examine the variable joypadState. The bits
     * in joypadState are manipulated to resemble what the game expects. When a
     * button is pressed, the corresponding bit in joypadState is set to 0 and
     * if it is not pressed it is set to 1.
     */
    private int joypadState
    private InterruptHandler interruptHandler

    int getJoypadState() {
        println 'Getting joypad state'
        int keyRegister = MemoryManager.rom[KEY_REGISTER]
        
        /*
         * Flip all bits so that 0 becomes 1 and 1 becomes zero so that we can
         * make use of the methods to check for a set bit. With the joypad, a
         * keypress is represented by 0, if we flip the bits, we can detect
         * keypresses as 1.
         */
        keyRegister = (keyRegister ^ 0xFF) * 0xFF

        /*
         * Is the game trying to read the standard buttons? Note that the
         * conditional check is inverted, because for the joypad 0 means the
         * button has been pressed and 1 means it has not.
         */
        if(!BitUtil.isSet(keyRegister, keyRegisterBit.DIRECTIONAL_BUTTON)) {
            /*
             * This bit shift is done because the directional button presses
             * are stored in the top nibble of the joypad's byte.
             */
            int joypad = joypadState >> 4
            
            /*
             * The logic that follows essentially sets bits 0 - 4 of keyRegister
             * to the upper nibble of joypadState.
             */
            
            /*
             * Next the upper nibble of the joypad is turned on. This preserves
             * the original value of the keyRegister before returning.
             */
            joypad = joypad | 0xF0
            
            /*
             * The upper nibble of the joypadState were stored in the lower
             * nibble of joypad and the upper nibble of joypad was set to 1111.
             * So when this logical AND is performed, the upper nibble of the
             * key register is preserved, and the lower nibble is set in
             * accordance to the lower nibble of joypad (which is really the
             * upper nibble of joypadState).
             */
            keyRegister = keyRegister & joypad
        }else if(!BitUtil.isSet(keyRegister, keyRegisterBit.STANDARD_BUTTON)) {
            /*
             * The standard buttons are stored in the lower nibble of
             * joypadState. This logic sets bits 0 - 4 of keyRegister to the
             * same values as the lower nibble of joypadState.
             */
            int joypad = joypadState & 0xF
            joypad = joypad | 0xF0
            keyRegister = keyRegister & joypad
        }
        
        return keyRegister
    }

    public void keyPressed(int keyCode) {
        
        if(inputMap[keyCode] == null) {
            println 'Invalid key pressed'
            return
        }
        
        println 'key press'

        int key = inputMap[keyCode]
        
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
        /*
         * Simply set the corresponding bit in joypadState.
         */
        joypadState = BitUtil.setBit(joypadState, key)
    }
}