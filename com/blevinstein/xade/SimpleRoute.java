package com.blevinstein.xade;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class SimpleRoute extends Route {
  private List<Point> points;
  private List<Double> sublength;
  private double length;

  public SimpleRoute(List<Point> points) {
    this.points = points;
   
    // calculate lengths
    sublength = new ArrayList<>();
    length = 0;
    for (int i = 0; i < points.size() - 1; i++) {
      double newlen = Point.dist(points.get(i), points.get(i+1));
      sublength.add(newlen);
      length += newlen;
    }
  }

  private Pair<Integer, Double> getRelativePosition(double position) {
    if (position < 0 || position > length()) {
      throw new IllegalArgumentException();
    }
    int segment = 0;
    double offset = position;
    while (offset > sublength.get(segment)) {
      offset -= sublength.get(segment);
      segment++;
    }
    return ImmutablePair.of(segment, offset);
  }

  public Point position(double t) {
    Pair<Integer, Double> relativePosition = getRelativePosition(t);

    int segment = relativePosition.getLeft();
    double offset = relativePosition.getRight();

    return Point.interpolate(points.get(segment), points.get(segment + 1),
        offset / sublength.get(segment));
  }

  public Point direction(double t) {
    int segment = getRelativePosition(t).getLeft();
    Point a = points.get(segment);
    Point b = points.get(segment + 1);
    return b.sub(a).norm();
  }

  public double length() { return length; }

  public Shape shape() {
    Path2D.Double path = new Path2D.Double();
    Point start = points.get(0);
    path.moveTo(start.getX(), start.getY());
    for (int i = 1; i < points.size(); i++) {
      Point p = points.get(i);
      path.lineTo(p.getX(), p.getY());
    }
    return path;
  }
}

