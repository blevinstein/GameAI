package com.blevinstein.xade;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public interface Drawable {
  Shape shape();
  default Color color() {
    return Color.WHITE;
  }
  default boolean fill() {
    return false;
  }

  static void draw(Graphics2D g, Drawable d) {
    draw(g, d, null);
  }
  static void draw(Graphics2D g, Drawable d, AffineTransform xfm) {
    Shape shape = xfm == null ? d.shape() : xfm.createTransformedShape(d.shape());
    g.setColor(d.color());
    if (d.fill()) {
      g.fill(shape);
    } else {
      g.draw(shape);
    }
  }
}
