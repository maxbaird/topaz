package org.topaz.cpu

class Register{
    int A
    int B
    int C
    int D
    int E
    int F
    int H
    int L

    int pc
    int sp

    /* flag bits */
    static final int FLAG_Z = 7
    static final int FLAG_N = 6
    static final int FLAG_H = 5
    static final int FLAG_C = 4
    
    public Register() {
        this.pc = 0x100
        this.sp = 0xFFFE
        
        this.setAF(0x01B0)
        this.setBC(0x0013)
        this.setDE(0x00D8)
        this.setHL(0x014D)
    }

    private void setFlag(int pos){
        this.F = this.F | (1 << pos)
    }

    private void clearFlag(int pos){
        this.F = ~(1 << pos) & this.F & 0xFF
    }

    boolean isSet(int pos){
        return this.F & (1 << pos)
    }

    private def combine(def b1, def b2){
        return b1 << 8 | b2
    }

    private def split(def s){
        def b1 = s >> 8
        def b2 = s & 0xFF
        return [b1, b2]
    }

    def getAF(){
        return combine(A,F)
    }

    def getBC(){
        return combine(B,C)
    }

    def getDE(){
        return combine(D,E)
    }

    def getHL(){
        return combine(H,L)
    }

    void setAF(def s){
        (A, F) = split(s)
    }

    void setBC(def s){
        (B, C) = split(s)
    }

    void setDE(def s){
        (D, E) = split(s)
    }

    void setHL(def s){
        (H, L) = split(s)
    }

    boolean isZ(){
        return isSet(FLAG_Z)
    }

    boolean isN(){
        return isSet(FLAG_N)
    }

    boolean isH(){
        return isSet(FLAG_H)
    }

    boolean isC(){
        return isSet(FLAG_C)
    }

    void setZ(){
        setFlag(FLAG_Z)
    }
    
    void clearZ() {
        clearFlag(FLAG_Z)
    }

    void setN(){
        setFlag(FLAG_N)
    }
    
    void clearN() {
        clearFlag(FLAG_N)
    }

    void setH(){
        setFlag(FLAG_H)
    }
    
    void clearH() {
        clearFlag(FLAG_H)    
    }

    void setC(){
        setFlag(FLAG_C) 
    }
    
    void clearC() {
        clearFlag(FLAG_C)
    }
    
    void clearAllFlags() {
        f = 0
    }

    def printFlags(){
        println String.format("%8s", Integer.toBinaryString(this.F)).replaceAll(" ", "0")
    }
}
