class Cartridge{
 static byte[] load(def path){
  File f = new File(path)
   byte[] b
   try{
    b = f.bytes
   }catch(Exception e){
    println "Error reading cartridge: " + e.toString()
   }
  return b
 }
}

