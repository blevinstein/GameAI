package com.blevinstein.xade;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

public class SimpleDrawable {
  private Color color;
  private boolean fill;
  private Shape shape;

  public SimpleDrawable(Shape shape) {
    this(shape, Color.WHITE, false);
  }

  public SimpleDrawable(Shape shape, Color color, boolean fill) {
    this.shape = shape;
    this.color = color;
    this.fill = fill;
  }

  public void draw(Graphics2D g) {
    g.setColor(color);
    if (fill) {
      g.fill(shape);
    } else {
      g.draw(shape);
    }
  }
}
