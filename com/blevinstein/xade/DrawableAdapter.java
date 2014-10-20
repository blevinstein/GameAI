package com.blevinstein.xade;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

public class DrawableAdapter implements Drawable {
  private final Shape shape;
  private final Color color;
  private final boolean fill;
  public DrawableAdapter(Shape shape) {
    this(shape, Color.WHITE, false);
  }
  public DrawableAdapter(Shape shape, Color color, boolean fill) {
    this.shape = shape;
    this.color = color;
    this.fill = fill;
  }
  public Shape shape() { return shape; }
  public Color color() { return color; }
  public boolean fill() { return fill; }
}
