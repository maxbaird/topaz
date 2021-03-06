package org.topaz.util

class BitUtil{
    static boolean isSet(int address, int position){
        return (address >> position) & 1
    }
    
    static int setBit(int address, int position){
        int mask = 1 << position
        address |= mask
        return address
    }
    
    static int clearBit(int address, int position){
        int mask = 1 << position
        address &= ~mask
        return address
    }
    
    static int getValue(int data, int position){
        int mask = 1 << position
        return (data & mask) ? 1 : 0
    }
}