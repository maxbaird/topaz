package org.topaz

import org.topaz.MemoryManager

class DMAHandler{
    /*
     * Writing to this register launches a DMA transfer from ROM or RAM to OAM
     * memory (the sprite attribute table). When the memory manager attempts to
     * write to this address, it triggers a copying of data to sprite RAM. The
     * CPU can only access the sprite attribute table during LCD mode 2.
     * 
     * The data written to this address is the *source address* of the data that
     * must be copied to the sprite RAM memory region (0xFE00 - 0xFE9F).
     */
    public static final int DMA_REGISTER = 0xFF46
    
    /*
     * These are the start and end address ranges for the Sprite Attribute Table
     * (OAM) as well as the number of bytes needed to fill the space between the
     * address range.
     */
    private final int SPRITE_OAM_START = 0xFE00
    private final int SPRITE_OAM_END = 0xFE9F
    private final int TOTAL_BYTES = (SPRITE_OAM_END - SPRITE_OAM_START) + 1
    
    MemoryManager memoryManager
    
    public void transfer(int data) {
        /*
         * The source address of the transfer is written to 0xFF46. The
         * destination is between addresses 0xFE00 and 0xFE9F which means a
         * total of 0xA0 bytes must be copied. The source address written to the
         * DMA_REGISTER (0xFF46) is divided by 100, so the correct source
         * address is obtained by multiplying by 100. The multiplication is done
         * by simply shifting to the left by 8 places.
         */
        
        int address = data << 8
        
        TOTAL_BYTES.times {it-> 
           memoryManager.writeMemory(SPRITE_OAM_START+it, memoryManager.readMemory(address+it))
        }
    }
    
}