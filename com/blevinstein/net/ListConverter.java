package com.blevinstein.net;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListConverter<T> implements Converter<List<T>> {
  private Converter<T> converter;
  private int n;

  public ListConverter(Converter<T> converter, int n) {
    this.converter = converter;
    this.n = n;
  }

  public Signal toSignal(List<T> values) {
    checkArgument(values.size() == n, "Incorrect number of values provided.");

    List<Double> result = new ArrayList<>();
    for (T value : values) {
      for (double bit : converter.toSignal(value).getRaw()) {
        result.add(bit);
      }
    }
    return new Signal(result);
  }

  public List<T> fromSignal(Signal signal) {
    int bits = converter.bits();
    double[] raw = signal.getRaw();
    List<Signal> signals = new ArrayList<>();
    for (int i = 0; i < raw.length; i += bits) {
      signals.add(new Signal(Arrays.copyOfRange(raw, i, bits)));
    }
    return Lists.transform(signals, s -> converter.fromSignal(s));
  }

  public int bits() {
    return converter.bits() * n;
  }
}
