package com.blevinstein.net;

import com.google.common.base.Converter;

public class BinaryConverter extends Converter<Boolean, Signal> {
  // implicit no-argument constructor
  // TODO(blevinstein): implements ProvidesBits, public int bits()?

  @Override
  public Signal doForward(Boolean b) {
    return new Signal(new double[] {b ? 1.0 : -1.0});
  }

  @Override
  public Boolean doBackward(Signal signal) {
    return signal.get(0) > 0.0;
  }

  public int bits() { return 1; }
}
