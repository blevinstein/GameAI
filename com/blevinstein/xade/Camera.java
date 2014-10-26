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
    xfm = new AffineTransform(width/w, 0, 0, height/h, center.getX() * width/w, center.getY() * height/h);
  }

  public AffineTransform getTransform() { return xfm; }
}
