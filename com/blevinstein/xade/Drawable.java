package com.blevinstein.xade;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

public interface Drawable {
  Shape shape();
  default Color color() {
    return Color.WHITE;
  }
  default boolean fill() {
    return false;
  }

  static void draw(Graphics2D g, Drawable d) {
    g.setColor(d.color());
    if (d.fill()) {
      g.fill(d.shape());
    } else {
      g.draw(d.shape());
    }
  }
}
