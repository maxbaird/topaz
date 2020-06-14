package org.topaz.debug

import java.lang.StringBuilder
import java.lang.String

class GPUDumper{
    final int PIXEL_AMT = 160
    StringBuilder sb = new StringBuilder()

    public int scrollx
    public int scrolly
    public int windowx
    public int windowy
    public int tileData
    public int backgroundMemory
    public int yPosition
    public int tileRow

    /* data within pixel loop */
    public int[] xPosition = new int[PIXEL_AMT]
    public int[] tileColumn = new int[PIXEL_AMT]
    public int[] tileNumber = new int[PIXEL_AMT]
    public int[] tileAddress = new int[PIXEL_AMT]
    public int[] tileLocation = new int[PIXEL_AMT]
    public int[] currentLine = new int[PIXEL_AMT] /* just line in gameboy.live code */
    public int[] pixelData1 = new int[PIXEL_AMT]
    public int[] pixelData2 = new int[PIXEL_AMT]
    public int[] colourBit = new int[PIXEL_AMT]
    public int[] colourNumber = new int[PIXEL_AMT]
    public int[] red = new int[PIXEL_AMT]
    public int[] green = new int[PIXEL_AMT]
    public int[] blue = new int[PIXEL_AMT]

    void dump(def iteration, def filename) {
        println 'Dumping GPU state: ' + filename
        sb.length = 0
        sb.append("Iteration: " + iteration + '\n')
        sb.append("scrollx: " + scrollx + '\n')
        sb.append("scrolly: " + scrolly + '\n')
        sb.append("windowx: " + windowx + '\n')
        sb.append("windowy: " + windowy + '\n')
        sb.append("tileData: " + tileData + '\n')
        sb.append("backgroundMemory: " + backgroundMemory + '\n')
        sb.append("yPosition: " + yPosition + '\n')
        sb.append("tileRow: " + tileRow + '\n')
        sb.append('===========\n')
        
        appendPixelData(sb, "xPosition", xPosition)
        appendPixelData(sb, "tileColumn", tileColumn)
        appendPixelData(sb, "tileNumber", tileNumber)
        appendPixelData(sb, "tileAddress", tileAddress)
        appendPixelData(sb, "tileLocation", tileLocation)
        appendPixelData(sb, "currentLine", currentLine)
        appendPixelData(sb, "pixelData1", pixelData1)
        appendPixelData(sb, "pixelData2", pixelData2)
        appendPixelData(sb, "colourBit", colourBit)
        appendPixelData(sb, "colourNumber", colourNumber)
        appendColours()
        
        new File(filename).newWriter().withWriter{w ->
            w << sb
        }
        
        println 'Dumped GPU state: ' + filename
    }
    
    private void appendPixelData(StringBuilder sb, String name, int[] pixelData) {
        pixelData.eachWithIndex{data, idx->
           sb.append(String.format("%s%d: %d\n", name, idx, data))
        }
        sb.append('===========\n')
    }
    
    private void appendColours() {
        PIXEL_AMT.times{
            sb.append(String.format("red: %d, green: %d, blue: %d\n", red[it], green[it], blue[it]))            
        }
    }
}