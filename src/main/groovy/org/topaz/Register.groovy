class Register{
  int A
  int B
  int C
  int D
  int E
  int F
  int H
  int L

  /* flag bits */
  private static int FLAG_Z = 7
  private static int FLAG_N = 6
  private static int FLAG_H = 5
  private static int FLAG_C = 4

 private void setFlag(int pos){
  this.F = this.F | (1 << pos)
 }

 private void clearFlag(int pos){
  this.F = ~(1 << pos) & this.F & 0xFF
 }

 private boolean isSet(int pos){
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

 void setZ(boolean z){
  z ? setFlag(FLAG_Z) : clearFlag(FLAG_Z)
 }

 void setN(boolean n){
  n ? setFlag(FLAG_N) : clearFlag(FLAG_N)
 }

 void setH(boolean h){
  h ? setFlag(FLAG_H) : clearFlag(FLAG_H)
 }

 void setC(boolean c){
  c ? setFlag(FLAG_C) : clearFlag(FLAG_C)
 }

 def printFlags(){
  println String.format("%8s", Integer.toBinaryString(this.F)).replaceAll(" ", "0")
 }

}
