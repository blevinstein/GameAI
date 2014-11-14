package com.blevinstein.net;

import com.google.common.base.Converter;

public class LetterConverter extends Converter<String, Signal> {

  public static String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  // does all the heavy lifting
  private EnumConverter slave = new EnumConverter(26);

  public Signal doForward(String value) {
    int loc = LETTERS.indexOf(value);
    if (loc == -1) {
      throw new IllegalArgumentException("Expected a letter, got " + value + "!");
    }
    return slave.convert(loc);
  }

  public String doBackward(Signal signal) {
    int loc = slave.reverse().convert(signal);
    return LETTERS.charAt(loc) + "";
  }

  public int bits() { return slave.bits(); }
}
