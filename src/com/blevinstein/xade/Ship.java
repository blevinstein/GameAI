package com.blevinstein.xade;

import java.awt.geom.Path2D;

@SuppressWarnings("serial")
public class Ship extends Path2D.Double {
  public Ship() {
    moveTo(1.5, 0.0);
    lineTo(-1.5, 1.0);
    lineTo(-0.5, 0.0);
    lineTo(-1.5, -1.0);
    lineTo(1.5, 0.0);
  }
}
