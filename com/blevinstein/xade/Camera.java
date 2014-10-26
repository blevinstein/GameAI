package com.blevinstein.xade;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.NoninvertibleTransformException;

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

  /*
   * Apply the transformation, world coords -> screen coords
   */
  public Point apply(Point p) {
    // Create a Point2D to store the result of the transformation
    Point2D dest = new Point2D.Double();
    return toPoint(xfm.transform(toPoint2D(p), dest));
  }

  /*
   * Apply the reverse transformation, screen coords -> world coords
   */
  public Point applyInverse(Point p) {
    // Create a Point2D to store the result of the transformation
    Point2D dest = new Point2D.Double();
    try {
      return toPoint(xfm.inverseTransform(toPoint2D(p), dest));
    } catch(NoninvertibleTransformException e) {
      throw new IllegalStateException();
    }
  }
 
  // Simple conversion functions
  private Point2D toPoint2D(Point p) { 
    return new Point2D.Double(p.getX(), p.getY());
  }
  private Point toPoint(Point2D p) {
    return new Point(p.getX(), p.getY());
  }
}
