package com.blevinstein.xade;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.NoninvertibleTransformException;

/*
 * Represents a view as an AffineTransform from input coords to screen coords
 */
public class Camera {
  // output
  private AffineTransform xfm = new AffineTransform();
  // output size
  protected double width = 1.0;
  protected double height = 1.0;
  // input size
  protected double min_width = 1.0;
  protected double min_height = 1.0;
  // input center
  protected Point center = new Point(0.0, 0.0);

  public Camera() {
    refocus();
  }

  // set the center of view, with respect to input coords
  public Camera center(Point center) {
    this.center = center;
    refocus();
    return this;
  }

  // set the width and height of the output
  public Camera output(double width, double height) {
    this.width = width;
    this.height = height;
    refocus();
    return this;
  }

  // set the width and height of the field of view
  // NOTE: output may include more than specified to fix aspect ratio
  public Camera input(double min_width, double min_height) {
    this.min_width = min_width;
    this.min_height = min_height;
    refocus();
    return this;
  }

  // called internally to recalculate the AffineTransform
  protected void refocus() {
    // recalculate scale
    double scale = Math.min(width / min_width, height / min_height);
    xfm = new AffineTransform();
    // NOTE: transformations appear in reverse order here
    // translate origin to middle of screen
    xfm.translate(width / 2, height / 2);
    // scale
    xfm.scale(scale, scale);
    // translate center to origin
    xfm.translate(-center.getX(), -center.getY());
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
