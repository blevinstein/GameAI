package com.blevinstein.xade;

public abstract class Route {
  public abstract Point get(double position);
  public abstract double length();
}

