package com.blevinstein.xade;

import java.awt.Shape;

// represents a route
public abstract class Route {
  // returns the position of a point along the route at time t
  // NOTE: |dposition/dt| = 1, i.e. constant velocity of 1
  // NOTE: 0 < t < length()
  public abstract Point position(double t);
  // returns dposition/dt as a normalized vector
  public abstract Point direction(double t);
  // returns the total length of the route
  public abstract double length();
  // returns a drawable representation of the route
  public abstract Shape shape();
}

