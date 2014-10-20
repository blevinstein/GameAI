package com.blevinstein.xade;

import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.util.List;

public class City implements Drawable {
  private Point location;
  private int carrying_capacity;
  private List<Army> occupiers;
  private double radius = 10.0;

  public Shape shape() {
    return new Ellipse2D.Double(location.getX(), location.getY(), radius * 2, radius * 2);
  }
}
