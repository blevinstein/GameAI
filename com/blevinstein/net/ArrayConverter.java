package com.blevinstein.net;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

public class ArrayConverter<T> implements Converter<T[]> {
  Converter<T> _converter;
  int _length;
  Class<T> _klass;

  // klass argument required to work around weaknesses in Java generics
  public ArrayConverter(Class<T> klass, Converter<T> converter, int length) {
    if (converter == null) throw new NullPointerException();
    _klass = klass;
    _converter = converter;
    _length = length;
  }

  public double[] toDoubles(T values[]) {
    if (values.length != _length)
      throw new RuntimeException("Wrong input size!");
    return Arrays.stream(values)
      .flatMapToDouble(v -> Arrays.stream(_converter.toDoubles(v)))
      .toArray();
  }

  @SuppressWarnings("unchecked")
  public T[] fromDoubles(double doubles[]) {
    int bits = _converter.bits();
    return Stream
      .iterate(0, i -> i + bits).limit(_length) // i = 0 step by bits
      .map(i -> _converter.fromDoubles(Arrays.copyOfRange(doubles, i, bits)))
      .toArray(len -> (T[]) Array.newInstance(_klass, _length));
  }

  public int bits() {
    return _converter.bits() * _length;
  }
}
