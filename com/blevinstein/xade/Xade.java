package com.blevinstein.xade;

import com.blevinstein.util.Throttle;
import com.blevinstein.util.Util;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.List;

@SuppressWarnings("serial")
class Xade extends JPanel implements KeyListener, ComponentListener {
  private final int FPS = 60;
  private boolean done = false;
  private World world = new World();
  private Camera camera = new Camera();

  public Xade() {
    // position camera
    camera.center(new Point(0.0, 0.0));
    camera.input(100.0, 100.0);

    List<Color> playerColors = Misc.chooseColors(4);
    List<Point> positions = ImmutableList.of(new Point(-50.0, -50.0), new Point(-50.0, 50.0),
        new Point(50.0, -50.0), new Point(50.0, 50.0));
    for (int i = 0; i < 4; i++) {
      // each player gets a city
      City newCity = new City(positions.get(i), 5.0);
      world.add(newCity);
      Player newPlayer = new Player()
        .setColor(playerColors.get(i));
      world.add(newPlayer);
    }
  }

  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    Graphics2D g2 = (Graphics2D) g;
    g2.setTransform(camera.getTransform());
    world.draw(g2);
  }

  // implements KeyListener
  public void keyPressed(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}

  // implements ComponentListener
  public void componentHidden(ComponentEvent e) {}
  public void componentMoved(ComponentEvent e) {}
  public void componentResized(ComponentEvent e) {
    camera.output(getWidth(), getHeight());
  }
  public void componentShown(ComponentEvent e) {}

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
    frame.addComponentListener(display);

    frame.setVisible(true);

    display.mainLoop();
  }
}

