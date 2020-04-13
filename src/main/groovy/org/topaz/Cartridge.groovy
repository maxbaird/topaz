import java.io.FileInputStream

class Cartridge{
 int mRomSize
 private File mRom

  public Cartridge(File mRom, int mRomSize){
    this.mRom = mRom
    this.mRomSize = mRomSize
  }

 byte[] load(){
   def inputStream = new FileInputStream(this.mRom)
   byte[] b = new byte[this.mRomSize]

   try{
     inputStream.read(b)
     inputStream.close()
   }catch(Exception e){
    println "Error reading cartridge: " + e.toString()
   }
  return b
 }
}
