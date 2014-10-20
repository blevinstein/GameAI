package com.blevinstein.xade;

import java.awt.geom.Ellipse2D;
import java.util.Objects;

public class OnRoute implements State {
  private final Route route;
  private final City destination;
  private final double speed;
  private final double position;

  public OnRoute(Route route, City destination, double speed) {
    this(route, destination, speed, 0.0);
  }

  public OnRoute(Route route, City destination, double speed, double position) {
    this.route = route;
    this.destination = destination;
    this.speed = speed;
    this.position = position;
  }

  public Drawable drawable() {
    Point location = route.position(position);
    // TODO
    // get direction
    // get Ship
    // apply rotation and translation
    return new DrawableAdapter(new Ellipse2D.Double(location.getX(), location.getY(), 1.0, 1.0));
  }

  public State update(double t) {
    double newPosition = position + t * speed;
    if (newPosition < route.length()) {
      return new OnRoute(route, destination, speed, newPosition);
    } else {
      return new InCity(destination);
    }
  }

  public int hashCode() {
    return Objects.hash(route, destination, speed, position);
  }

  public boolean equals(Object obj) {
    if (obj instanceof OnRoute) {
      OnRoute other = (OnRoute) obj;
      if (this.route.equals(other.route) && this.destination.equals(other.destination)
          && this.speed == other.speed && this.position == other.position) {
        return true;
      }
    }
    return false;
  }
}
