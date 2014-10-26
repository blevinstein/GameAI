package com.blevinstein.xade;

import java.awt.geom.AffineTransform;

public class Camera {
  // output
  private double width;
  private double height;
  private AffineTransform xfm;
  // focus state
  Point center;
  double w;
  double h;

  public Camera(double width, double height) {
    this.width = width;
    this.height = height;
    xfm = new AffineTransform();
  }

  public void resize(double width, double height) {
    this.width = width;
    this.height = height;
    focus(center, w, h);
  }

  public void focus(Point center, double w, double h) {
    this.center = center;
    this.w = w;
    this.h = h;
    // translate center to middle of screen
    xfm = AffineTransform.getTranslateInstance(width / 2 - center.getX(), height / 2 - center.getY());
    // scale
    double scale = Math.min(width / w, height / h);
    xfm.scale(scale, scale);
  }

  public AffineTransform getTransform() { return xfm; }
}
