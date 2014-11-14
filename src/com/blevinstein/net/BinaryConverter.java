package com.blevinstein.net;

public class BinaryConverter implements Converter<Boolean> {
  // implicit no-argument constructor

  public Signal toSignal(Boolean b) {
    return new Signal(new double[] {b ? 1.0 : -1.0});
  }

  public Boolean fromSignal(Signal signal) {
    return signal.get(0) > 0.0;
  }

  public int bits() { return 1; }
}
