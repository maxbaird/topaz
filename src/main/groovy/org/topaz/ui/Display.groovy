package org.topaz.ui

import org.topaz.gpu.GPU
import org.topaz.gpu.LCD
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.JFrame
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Dimension
//import java.awt.EventQueue

class Display extends JPanel{
    private BufferedImage canvas
    JFrame frame
    private int displayHeight 
    private int displayWidth 

    public Display() {
        displayHeight = LCD.HEIGHT
        displayWidth = LCD.WIDTH
        this.canvas = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_RGB)
        createGUI()
        //clearDisplay()
    }
    
    private void createGUI() {
        frame = new JFrame()
        frame.setResizable(false)
        frame.setLocationRelativeTo(null)
        frame.setSize(new Dimension(displayWidth, displayHeight))
        frame.setTitle("GroovyBoy")
        frame.add(this)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.setVisible(true)
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g)
        Graphics2D g2D = (Graphics2D)g
        g2D.drawImage(this.canvas, null, null)
    }

//    private clearDisplay() {
//        /*
//         * Clears screen to white
//         */
//        displayWidth.times{x->
//            displayHeight.times {y->
//                this.canvas.setRGB(x, y, Color.WHITE.getRGB())
//            }
//        }
//    }
    
    private update(int[][][] screenData) {
        Color c
        displayWidth.times{x->
            displayHeight.times {y->
                c = new Color(screenData[x][y][0], screenData[x][y][1], screenData[x][y][2])
                this.canvas.setRGB(x, y, c.RGB)
            }
        }
        repaint() 
    }
}