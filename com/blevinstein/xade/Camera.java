package com.blevinstein.xade;

import java.awt.geom.AffineTransform;

public class Camera {
  // output
  private double width;
  private double height;
  private AffineTransform xfm;
  // focus state
  Point center;
  private double scale;

  public Camera(double width, double height) {
    this.width = width;
    this.height = height;
    xfm = new AffineTransform();
  }

  public void resize(double width, double height) {
    this.width = width;
    this.height = height;
    focus(center, scale);
  }

  public void focus(Point center, double min_width, double min_height) {
    focus(center, Math.min(width / min_width, height / min_height));
  }
  public void focus(Point center, double scale) {
    this.center = center;
    this.scale = scale;
    // translate center to middle of screen
    xfm = AffineTransform.getTranslateInstance(width / 2 - center.getX(), height / 2 - center.getY());
    // scale
    xfm.scale(scale, scale);
  }

  public AffineTransform getTransform() { return xfm; }
}
