package com.blevinstein.xade;

import java.util.Objects;

public class InCity implements State {
  private final City city;

  public InCity(City city) {
    this.city = city;
  }

  public City getCity() { return city; }

  public Point location() {
    return city.getLocation();
  }

  public Drawable drawable() {
    return null; // not drawable
  }

  public State update(double t) {
    return this;
  }

  public int hashCode() {
    return Objects.hash(city);
  }

  public boolean equals(Object obj) {
    if (obj instanceof InCity) {
      InCity other = (InCity) obj;
      if (this.city.equals(other.city)) {
        return true;
      }
    }
    return false;
  }
}
