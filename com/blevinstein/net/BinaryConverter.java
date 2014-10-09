package com.blevinstein.net;

public class BinaryConverter implements Converter<Boolean> {
  // implicit no-argument constructor

  public double[] toDoubles(Boolean b) {
    return new double[] {b ? 1.0 : -1.0};
  }

  public Boolean fromDoubles(double doubles[]) {
    return doubles[0] > 0.0;
  }

  public int bits() { return 1; }
}
