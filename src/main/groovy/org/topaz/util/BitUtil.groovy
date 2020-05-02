package org.topaz.util

class BitUtil{
    static boolean testBit(int address, int position){
        return (address >> position) & 1
    }
}