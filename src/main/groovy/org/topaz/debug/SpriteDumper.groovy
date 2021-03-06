package org.topaz.debug

import java.lang.StringBuilder

class SpriteDumper{
    final int SPRITE_TILE_AMT = 40
    final int TILE_PIXEL_AMT = 7

    StringBuilder sb = new StringBuilder()

    public boolean use8x16
    public int lcdControl

    public int[] index = new int[SPRITE_TILE_AMT]
    public int[] yPosition = new int[SPRITE_TILE_AMT]
    public int[] xPosition = new int[SPRITE_TILE_AMT]
    public int[] tileLocation = new int[SPRITE_TILE_AMT]
    public int[] attributes = new int[SPRITE_TILE_AMT]
    public int[] yFlip = new int[SPRITE_TILE_AMT]
    public int[] xFlip = new int[SPRITE_TILE_AMT]
    public int[] scanline = new int[SPRITE_TILE_AMT]
    public int[] ysize = new int[SPRITE_TILE_AMT]
    public int[] line = new int[SPRITE_TILE_AMT]
    public int[] dataAddress = new int[SPRITE_TILE_AMT]
    public int[] data1 = new int[SPRITE_TILE_AMT]
    public int[] data2 = new int[SPRITE_TILE_AMT]

    public int[][] colorbit = new int[SPRITE_TILE_AMT][TILE_PIXEL_AMT]
    public int[][] colorNum = new int[SPRITE_TILE_AMT][TILE_PIXEL_AMT]
    public int[][] colorAddress = new int[SPRITE_TILE_AMT][TILE_PIXEL_AMT]
    //public int[][] color = new int[SPRITE_TILE_AMT][TILE_PIXEL_AMT]
    //public int[][] xPix = new int[SPRITE_TILE_AMT][TILE_PIXEL_AMT]
    public int[][] pixel = new int[SPRITE_TILE_AMT][TILE_PIXEL_AMT]

    public void dump(int iteration, String filename) {
        println 'Dumping sprite data: ' + filename
        sb.length = 0

        sb.append("use8x16: " + use8x16 + '\n')
        sb.append("lcdControl: " + lcdControl + '\n')
        sb.append('===========\n')

        appendSpriteData(sb, "index", index)
        appendSpriteData(sb, "yPosition", yPosition)
        appendSpriteData(sb, "xPosition", xPosition)
        appendSpriteData(sb, "tileLocation", tileLocation)
        appendSpriteData(sb, "attributes", attributes)
        appendSpriteData(sb, "yFlip", yFlip)
        appendSpriteData(sb, "xFlip", xFlip)
        appendSpriteData(sb, "scanline", scanline)
        appendSpriteData(sb, "ysize", ysize)
        appendSpriteData(sb, "line", line)
        appendSpriteData(sb, "dataAddress", dataAddress)
        appendSpriteData(sb, "data1", data1)
        appendSpriteData(sb, "data2", data2)

        appendPixelSpriteData(sb, "colorbit", colorbit)
        appendPixelSpriteData(sb, "colorNum", colorNum)
        appendPixelSpriteData(sb, "colorAddress", colorAddress)
        //appendPixelSpriteData(sb, "color", color)
        //appendPixelSpriteData(sb, "xPix", xPix)
        appendPixelSpriteData(sb, "pixel", pixel)

        new File(filename).newWriter().withWriter{w ->
            w << sb
        }

        println 'Dumped sprite data: ' + filename
    }

    private void appendSpriteData(StringBuilder sb, String name, int[]spriteData) {
        spriteData.eachWithIndex{data, idx ->
            sb.append(String.format("%s%d: %d\n", name, idx, data))
        }
        sb.append('===========\n')
    }

    private void appendPixelSpriteData(StringBuilder sb, String name, int[][]pixelData) {
        pixelData.eachWithIndex{data, i ->
            data.eachWithIndex{data2, j ->
                sb.append(String.format("%s%d-%d: %d\n", name, i, j, data2))
            }
        }
        sb.append('===========\n')
    }
}