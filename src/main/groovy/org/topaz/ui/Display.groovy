package org.topaz.ui

import org.topaz.gpu.LCD
import org.topaz.Joypad
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.JFrame
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import groovy.transform.*

@CompileStatic
class Display extends JPanel implements KeyListener{
    private BufferedImage canvas
    private JFrame frame
    private int displayHeight 
    private int displayWidth 
    private Joypad joypad

    public Display(Joypad joypad) {
        displayHeight = LCD.HEIGHT
        displayWidth = LCD.WIDTH
        this.canvas = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_RGB)
        this.joypad = joypad
        createGUI()
        //clearDisplay()
    }
    
    private void createGUI() {
        frame = new JFrame()
        this.setPreferredSize(new Dimension(displayWidth, displayHeight))
        frame.add(this)
        frame.setResizable(false)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.setTitle("GroovyBoy")
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.setVisible(true)
        
        this.addKeyListener(this)
        this.focusable = true
        this.requestFocusInWindow()
        println this.getSize().height
        println this.getSize().width
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
    
    public update(int[][][] screenData) {
        Color c
        displayWidth.times{x->
            displayHeight.times {y->
                c = new Color(screenData[x][y][0], screenData[x][y][1], screenData[x][y][2])
                this.canvas.setRGB(x, y, c.RGB)
            }
        }
        repaint() 
    }

    @Override
    public void keyPressed(KeyEvent k) {
        joypad.keyPressed(k.keyCode)
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        
    }
}