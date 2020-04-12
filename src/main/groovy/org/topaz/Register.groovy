class Register{
  def A
  def B
  def C
  def D
  def E
  def F
  def H
  def L

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

}
