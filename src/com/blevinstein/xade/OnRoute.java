package com.blevinstein.xade;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.Objects;

public class OnRoute implements State {
  private final Route route;
  private final City destination;
  private final double speed;
  private final double distance;

  public OnRoute(Route route, City destination, double speed) {
    this(route, destination, speed, 0.0);
  }

  private OnRoute(Route route, City destination, double speed, double distance) {
    this.route = route;
    this.destination = destination;
    this.speed = speed;
    this.distance = distance;
  }

  public Point location() {
    return route.position(distance);
  }

  public Drawable drawable() {
    Point location = route.position(distance);
    return (g) -> {
      g.setColor(Color.WHITE);
      g.draw(route.shape());
    };
  }

  public State update(double t) {
    double newDistance = distance + t * speed;
    if (newDistance < route.length()) {
      return new OnRoute(route, destination, speed, newDistance);
    } else {
      return new InCity(destination);
    }
  }

  public int hashCode() {
    return Objects.hash(route, destination, speed, distance);
  }

  public boolean equals(Object obj) {
    if (obj instanceof OnRoute) {
      OnRoute other = (OnRoute) obj;
      if (this.route.equals(other.route) && this.destination.equals(other.destination)
          && this.speed == other.speed && this.distance == other.distance) {
        return true;
      }
    }
    return false;
  }
}
