package com.blevinstein.net;

import com.google.common.base.Converter;
import com.google.common.collect.Lists;

import java.util.List;

public class BinaryConverter extends Converter<List<Boolean>, Signal> {
  // implicit no-argument constructor
  // TODO(blevinstein): implements ProvidesBits, public int bits()?

  @Override
  public Signal doForward(List<Boolean> bools) {
    return new Signal(Lists.transform(bools, (b) -> b ? 1.0 : -1.0));
  }

  @Override
  public List<Boolean> doBackward(Signal signal) {
    return Lists.transform(signal.getVectorList(),
        (d) -> d > 0.0);
  }

  public int bits() { return 1; }
}
