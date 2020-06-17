package org.topaz.cpu;

public class Register2{
    public int A;
    public int B;
    public int C;
    public int D;
    public int E;
    public int F;
    public int H;
    public int L;

    public int pc;
    public int sp;

    /* flag bits */
    public static final int FLAG_Z = 7;
    public static final int FLAG_N = 6;
    public static final int FLAG_H = 5;
    public static final int FLAG_C = 4;

    public Register2() {
        this.pc = 0x100;
        this.sp = 0xFFFE;

        this.setAF(0x01B0);
        this.setBC(0x0013);
        this.setDE(0x00D8);
        this.setHL(0x014D);

        this.setZ(true);
        this.setN(false);
        this.setH(true);
        this.setC(true);
    }
    
    private void setFlag(int pos){
        this.F = (this.F | (1 << pos)) & 0xFF;
    }

    private void clearFlag(int pos){
        this.F = ~(1 << pos) & this.F & 0xFF;
    }

    private boolean isSet(int pos){
        return ((this.F >> pos) & 1) == 1;
    }

    private int combine(int b1, int b2){
        return b1 << 8 | b2;
    }

    private int[] split(int s){
        int[] b = new int[2];
        b[0] = s >> 8;
        b[1] = s & 0xFF;
        return b;
    }

    public int getSPLow() {
        return split(sp)[1];
    }

    public int getSPHigh() {
        return split(sp)[0];
    }

    public int getAF(){
        return combine(A,F);
    }

    public int getBC(){
        return combine(B,C);
    }

    public int getDE(){
        return combine(D,E);
    }

    public int getHL(){
        return combine(H,L);
    }

    public void setAF(int s){
        int b[] = new int[2];
        b = split(s); 
        A = b[0];
        F = b[1];
    }

    public void setBC(int s){
        int b[] = new int[2];
        b = split(s); 
        B = b[0];
        C = b[1];
    }

    public void setDE(int s){
        int b[] = new int[2];
        b = split(s); 
        D = b[0];
        E = b[1];
    }

    public void setHL(int s){
        int b[] = new int[2];
        b = split(s); 
        H = b[0];
        L = b[1];
    }

    public boolean isZ(){
        return isSet(FLAG_Z);
    }

    public boolean isN(){
        return isSet(FLAG_N);
    }

    public boolean isH(){
        return isSet(FLAG_H);
    }

    public boolean isC(){
        return isSet(FLAG_C);
    }

    public void setZ(){
        setFlag(FLAG_Z);
    }

    public void clearZ() {
        clearFlag(FLAG_Z);
    }

    public void setZ(boolean b) {
        if(b) {
            setFlag(FLAG_Z);
        }else {
            clearFlag(FLAG_Z);
        }
    }

    public void setN(){
        setFlag(FLAG_N);
    }

    public void clearN() {
        clearFlag(FLAG_N);
    }

    public void setN(boolean b) {
        if(b) {
            setFlag(FLAG_N); 
        }else {
            clearFlag(FLAG_N);
        }
    }

    public void setH(){
        setFlag(FLAG_H);
    }

    public void clearH() {
        clearFlag(FLAG_H);
    }

    public void setH(boolean b) {
        if(b) {
            setFlag(FLAG_H); 
        }else {
            clearFlag(FLAG_H);
        }
    }

    public void setC(){
        setFlag(FLAG_C);
    }

    public void clearC() {
        clearFlag(FLAG_C);
    }

    public void setC(boolean b) {
        if(b) {
            setFlag(FLAG_C);
        }else{
            clearFlag(FLAG_C);
        }
    }

    public void clearAllFlags() {
        F = 0;
    }

    public void printFlags(){
        System.out.println(String.format("%8s", Integer.toBinaryString(this.F)).replaceAll(" ", "0"));
    }

    @Override
    public String toString(){
        String str = "A: " + A + "\nB: " + B + "\nC: " + C + "\nD: " + D + "\nE: " + E + "\nF: " + F + "\nHL: " + getHL() + "\n";
        str += "===========\n";
        str += "Z: " + isZ() + "\nN: " + isN() + "\nH: " + isH() + "\nC: " + isC() + "\n";
        str += "===========\n";
        str += "sp: " + sp + "\npc: " + pc;
        str += "\n";
        return str;
    }
}

