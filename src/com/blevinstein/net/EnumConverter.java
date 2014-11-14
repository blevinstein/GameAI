package com.blevinstein.net;

import com.google.common.base.Converter;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;

// Represents a conversion between a neural network and a finite selection.
//
// E.g. ImageClassifier can match letters using an EnumConverter(26), which is
// represented by an int 0-25, or as 26 neuron inputs.

public class EnumConverter extends Converter<Integer, Signal> {
  int n;

  public EnumConverter(int n) {
    this.n = n;
  }

  @Override
  public Signal doForward(Integer value) {
    checkArgument(value >= 0 && value < n,
        String.format("Value %d is not between 0 and %d.", value, n));

    double[] doubles = new double[n];
    Arrays.fill(doubles, -1.0);
    doubles[value] = 1.0;
    return new Signal(doubles);
  }

  @Override
  public Integer doBackward(Signal signal) {
    checkArgument(signal.size() == n,
        String.format("Expected %d bits but received %d.", n, signal.size()));

    // Returns the index of the largest input
    int result = 0;
    for (int i = 1; i < n; i++) {
      if (signal.get(i) > signal.get(result)) {
        result = i;
      }
    }
    return result;
  }

  public int bits() { return n; }
}
