package com.blevinstein.xade;

import com.blevinstein.util.Throttle;
import com.blevinstein.util.Util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
class Xade extends JPanel implements KeyListener {
  private final int FPS = 60;
  private boolean done = false;
  private World world = new World();

  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    Graphics2D g2 = (Graphics2D) g;
    world.draw(g2);
  }

  public void keyPressed(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}

  private void mainLoop() {
    done = false;
    Throttle throttle = new Throttle(FPS); // target frame rate
    while (!done) {
      world.step(1.0/FPS);
      repaint();
      throttle.sleep();
    }
  }
  private void stop() { done = true; }

  // DRIVER
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    //frame.setSize(1024, 768 + 25);
    frame.setSize(640, 320 + 25);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Xade display = new Xade();
    frame.add(display);
    frame.addKeyListener(display);

    frame.setVisible(true);

    display.mainLoop();
  }
}

