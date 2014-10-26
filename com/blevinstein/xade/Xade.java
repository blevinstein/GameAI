package com.blevinstein.xade;

import com.blevinstein.util.Throttle;
import com.blevinstein.util.Util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
class Xade extends JPanel implements KeyListener {
  private boolean done = false;

  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());
  }

  public void keyPressed(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}

  private void mainLoop() {
    done = false;
    Throttle throttle = new Throttle(60); // target frame rate
    while (!done) {
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

