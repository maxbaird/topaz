import java.io.FileInputStream
class Cartridge{

 private static final int ROM_SIZE = 0x2000000

 static byte[] load(def path){
  def f = new File(path)
  def inputStream = new FileInputStream(f)
   byte[] b = new byte[ROM_SIZE]

   try{
    inputStream.read(b)
    inputStream.close()
   }catch(Exception e){
    println "Error reading cartridge: " + e.toString()
   }
  return b
 }

}
