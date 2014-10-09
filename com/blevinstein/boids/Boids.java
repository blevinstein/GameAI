package com.blevinstein.boids;

import com.blevinstein.util.Throttle;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;

// This is the driver for a program that simulates a flock of flying "boids"
// which are trained (as a neural net or as a population) to seek food, avoid
// collisions, etc.
//
// Minimal information about the environment is given to the boids.

class Boids extends JPanel implements KeyListener, MouseListener {

  private String HELP =
    "Help goes here.";

  @SuppressWarnings("unchecked")
  public Boids() {
    super(null);

    // receive key events
    this.setFocusable(true);
    this.addKeyListener(this);
  }

  public void run() {
    Throttle t = new Throttle(100); // 100fps max
    while (true) {
      // arena.update()
      repaint();
      t.sleep();
    }
  }

  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());
    
    // draw the boids
    // for each boid
    //   boid.draw(g, 0, 0, sx, sy);
  }

  public void keyPressed(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setSize(1024, 768+25);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Boids boids = new Boids();
    frame.add(boids);

    frame.setVisible(true);

    new Thread(() -> boids.run()).start();
  }

  private static final long serialVersionUID = 1;
}
