package org.topaz.cpu;
import org.topaz.util.UInt;

public class Register2{

    public UInt A = new UInt(UInt.EIGHT_BITS);
    public UInt B = new UInt(UInt.EIGHT_BITS);
    public UInt C = new UInt(UInt.EIGHT_BITS);
    public UInt D = new UInt(UInt.EIGHT_BITS);
    public UInt E = new UInt(UInt.EIGHT_BITS);
    public UInt F = new UInt(UInt.EIGHT_BITS);
    public UInt H = new UInt(UInt.EIGHT_BITS);
    public UInt L = new UInt(UInt.EIGHT_BITS);

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
        this.F.setValue((this.F.getValue() | (1 << pos)) & 0xFF);
    }

    private void clearFlag(int pos){
        this.F.setValue(~(1 << pos) & this.F.getValue() & 0xFF);
    }

    private boolean isSet(int pos){
        return ((this.F.getValue() >> pos) & 1) == 1;
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
        return combine(A.getValue(),F.getValue());
    }

    public int getBC(){
        return combine(B.getValue(),C.getValue());
    }

    public int getDE(){
        return combine(D.getValue(),E.getValue());
    }

    public int getHL(){
        return combine(H.getValue(),L.getValue());
    }

    public void setAF(int s){
        int b[] = new int[2];
        b = split(s); 
        A.setValue(b[0]);
        F.setValue(b[1]);
    }

    public void setBC(int s){
        int b[] = new int[2];
        b = split(s); 
        B.setValue(b[0]);
        C.setValue(b[1]);
    }

    public void setDE(int s){
        int b[] = new int[2];
        b = split(s); 
        D.setValue(b[0]);
        E.setValue(b[1]);
    }

    public void setHL(int s){
        int b[] = new int[2];
        b = split(s); 
        H.setValue(b[0]);
        L.setValue(b[1]);
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
        F.setValue(0);
    }

    public int getA() {
        return A.getValue();
    }

    public void setA(int a) {
        A.setValue(a);;
    }

    public int getB() {
        return B.getValue();
    }

    public void setB(int b) {
        B.setValue(b);
    }

    public int getC() {
        return C.getValue();
    }

    public void setC(int c) {
        C.setValue(c);
    }

    public int getD() {
        return D.getValue();
    }

    public void setD(int d) {
        D.setValue(d);
    }

    public int getE() {
        return E.getValue();
    }

    public void setE(int e) {
        E.setValue(e);
    }

    public int getF() {
        return F.getValue();
    }

    public void setF(int f) {
        F.setValue(f);
    }

    public int getH() {
        return H.getValue();
    }

    public void setH(int h) {
        H.setValue(h);
    }

    public int getL() {
        return L.getValue();
    }

    public void setL(int l) {
        L.setValue(l);
    }
    public void printFlags(){
        System.out.println(String.format("%8s", Integer.toBinaryString(this.F.getValue())).replaceAll(" ", "0"));
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

