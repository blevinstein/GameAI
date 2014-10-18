package com.blevinstein.xade;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

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

  public Point get(double position) {
    Pair<Integer, Double> relativePosition = getRelativePosition(position);

    int segment = relativePosition.getLeft();
    double offset = relativePosition.getRight();

    return Point.interpolate(points.get(segment), points.get(segment + 1),
        offset / sublength.get(segment));
  }

  public double length() { return length; }
}

