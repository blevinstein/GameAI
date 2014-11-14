package com.blevinstein.net;

public interface Converter<T> {
  public Signal toSignal(T value);
  public T fromSignal(Signal signal);
  public int bits(); // return the bit width of this converter
}
