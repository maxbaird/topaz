package org.topaz.util;

import java.lang.IllegalStateException;

public class UInt{
    public static final int EIGHT_BITS = 8;
    public static final int SIXTEEN_BITS = 16;
    
    private int mask;
    private int value;
    
    public UInt(int width){
        if(width == EIGHT_BITS){
            mask = 0xFF;
        }else if(width == SIXTEEN_BITS){
            mask = 0xFFFF;
        }else{
            throw new IllegalStateException("Unsupported bit width " + width + " specified.");
        }
        
        this.value = 0 & mask;
    }
    
    private void verify(int value){
        if(value < 0 || value > mask){
            throw new IllegalStateException("Value " + value + " not in range.");
        }
    }
    
    public void setValue(int value){
        verify(value);
        this.value = value & mask;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public void add(int value){
        //verify(value);
        this.value = (this.value + value) & mask;
    }
    
    public void sub(int value){
        //verify(value);
        this.value = (this.value - value) & mask;
    }
    
    public void inc() {
        this.value = (this.value + 1) & mask;
    }
    
    public void dec() {
        this.value = (this.value - 1) & mask;
    }
    
    @Override
    public String toString() {
       return String.format("%s", this.value); 
    }
}