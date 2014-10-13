package com.blevinstein.boids;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Boids extends JPanel {
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setSize(1024, 768 + 25);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel panel = new JPanel();
    frame.add(panel);

    BoidWorld world = new BoidWorld(100, panel);

    frame.setVisible(true);

    world.mainloop();
  }
  
  private static final long serialVersionUID = 1;
}
