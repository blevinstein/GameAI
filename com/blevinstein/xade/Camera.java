package com.blevinstein.xade;

import java.awt.geom.AffineTransform;

// TODO(blevinstein): add tests

public class Camera {
  // output
  private AffineTransform xfm = new AffineTransform();
  // output size
  private double width = 1.0;
  private double height = 1.0;
  // input size
  private double min_width = 1.0;
  private double min_height = 1.0;
  // input center
  Point center = new Point(0.0, 0.0);

  public Camera() {
    refocus();
  }

  public void center(Point center) {
    this.center = center;
    refocus();
  }

  public void output(double width, double height) {
    this.width = width;
    this.height = height;
    refocus();
  }

  public void input(double min_width, double min_height) {
    this.min_width = min_width;
    this.min_height = min_height;
    refocus();
  }

  public void refocus() {
    // recalculate scale
    double scale = Math.min(width / min_width, height / min_height);
    // translate center to middle of screen
    xfm = AffineTransform.getTranslateInstance(width / 2 - center.getX(), height / 2 - center.getY());
    // scale
    xfm.scale(scale, scale);
  }

  public AffineTransform getTransform() { return xfm; }
}
