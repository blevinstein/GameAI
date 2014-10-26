package com.blevinstein.xade;

import com.blevinstein.util.Throttle;
import com.blevinstein.util.Util;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
    camera.input(1000.0, 1000.0);

    // setup basic map
    List<Color> playerColors = Misc.chooseColors(4);
    double side = 400.0;
    List<Point> positions = ImmutableList.of(new Point(-side, -side), new Point(-side, side),
        new Point(side, -side), new Point(side, side));
    for (int i = 0; i < 4; i++) {
      // create a city
      // TODO(blevinstein): world.createCity()
      City newCity = new City(positions.get(i), 50.0);
      world.add(newCity);
      // TODO(blevinstein): world.createPlayer()
      // create a player
      Player newPlayer = new Player()
        .setColor(playerColors.get(i));
      world.add(newPlayer);
      // put the player in the city
      newCity.add(newPlayer, 1);
    }
  }

  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    Graphics2D g2 = (Graphics2D) g;
    g2.setTransform(camera.getTransform());
    g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON));
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

