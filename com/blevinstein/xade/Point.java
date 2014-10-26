package com.blevinstein.xade;

import java.util.Objects;

public class Point {
  private double x;
  private double y;

  public Point() {
    this(0.0, 0.0);
  }

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() { return x; }
  public double getY() { return y; }

  public Point add(Point other) {
    return new Point(x + other.x, y + other.y);
  }

  public Point sub(Point other) {
    return new Point(x - other.x, y - other.y);
  }

  public Point times(double scalar) {
    return new Point(x * scalar, y * scalar);
  }

  public double mag2() {
    return x*x + y*y;
  }

  public double mag() {
    return Math.sqrt(mag2());
  }

  // return normalized, so that mag() = 1
  public Point norm() {
    return this.times(1.0/mag());
  }

  public static double dist(Point a, Point b) {
    return a.sub(b).mag();
  }

  // interpolate(a, b, 0) => a
  // interpolate(a, b, 1) => b
  // interpolate(a, b, 0.5) => (a + b) / 2
  public static Point interpolate(Point a, Point b, double t) {
    // return a + (b - a) * t
    return a.add(b.sub(a).times(t));
  }

  public boolean equals(Point other, double tol) {
    return Math.abs(x - other.x) < tol
      && Math.abs(y - other.y) < tol;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Point) {
      Point other = (Point) obj;
      return equals(other, 0.01);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }
}
