package com.blevinstein.xade;

import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.util.List;
import java.util.Map;

public class City implements Drawable {
  private Point location;
  private double radius = 10.0;

  public City(Point location, double radius) {
    this.location = location;
    this.radius = radius;
  }
  
  public Point getLocation() { return location; }

  public double getRadius() { return radius; }

  // implements Drawable
  public Shape shape() {
    return new Ellipse2D.Double(location.getX(), location.getY(), radius * 2, radius * 2);
  }

  // occupying state
  private Map<Player,Integer> occupiers;
}

