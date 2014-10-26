package com.blevinstein.xade;

import java.awt.geom.AffineTransform;

public class Camera {
  private double width;
  private double height;
  private AffineTransform xfm;

  public Camera(double width, double height) {
    this.width = width;
    this.height = height;
    xfm = new AffineTransform();
  }

  public void resize(double width, double height) {
    this.width = width;
    this.height = height;
  }

  public void focus(Point center, double w, double h) {
    // translate center to middle of screen
    xfm = AffineTransform.getTranslateInstance(width / 2 - center.getX(), height / 2 - center.getY());
    // scale
    xfm.scale(width / w, height / h);
  }

  public AffineTransform getTransform() { return xfm; }
}
