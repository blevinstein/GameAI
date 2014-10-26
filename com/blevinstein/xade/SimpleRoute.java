package com.blevinstein.xade;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class SimpleRoute extends Route {
  private List<Point> points;
  private List<Double> sublengths;
  private double length;

  public SimpleRoute(List<Point> points) {
    this.points = points;
   
    // calculate lengths
    sublengths = new ArrayList<>();
    length = 0;
    for (int i = 0; i < points.size() - 1; i++) {
      double newlen = Point.dist(points.get(i), points.get(i+1));
      sublengths.add(newlen);
      length += newlen;
    }
  }

  private Pair<Integer, Double> getRelativePosition(double position) {
    if (position < 0 || position > length()) {
      throw new IllegalArgumentException();
    }
    int segment = 0;
    double offset = position;
    while (offset > sublengths.get(segment)) {
      offset -= sublengths.get(segment);
      segment++;
    }
    return ImmutablePair.of(segment, offset);
  }

  public int getPointCount() { return points.size(); }
  public Point getPoint(int i) {
    if (i < 0 || i >= points.size()) {
      throw new IllegalArgumentException();
    }
    return points.get(i);
  }

  public Point position(double t) {
    Pair<Integer, Double> relativePosition = getRelativePosition(t);

    int segment = relativePosition.getLeft();
    double offset = relativePosition.getRight();

    return Point.interpolate(points.get(segment), points.get(segment + 1),
        offset / sublengths.get(segment));
  }

  public Point direction(double t) {
    int segment = getRelativePosition(t).getLeft();
    Point a = points.get(segment);
    Point b = points.get(segment + 1);
    return b.sub(a).norm();
  }

  public double length() { return length; }

  @Override
  public void draw(Graphics2D g) {
    g.setColor(Color.BLACK);
    Shape path = shape(); // TODO(blevinstein): refactor
    g.draw(path);
  }

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

  public static SimpleRoute shortest(City a, City b) {
    // ab = unit vector from a to b
    Point ab = b.getLocation().sub(a.getLocation()).norm();
    // a_edge = a_loc + ab * radius
    Point a_edge = a.getLocation().add(ab.times(a.getRadius()));
    // b_edge = b_loc - ab * radius
    Point b_edge = b.getLocation().sub(ab.times(b.getRadius()));
    return new SimpleRoute(ImmutableList.of(a_edge, b_edge));
  }
}

