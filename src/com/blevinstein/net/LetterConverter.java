package com.blevinstein.net;

public class LetterConverter implements Converter<String> {

  public static String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  // does all the heavy lifting
  private EnumConverter slave = new EnumConverter(26);

  public Signal toSignal(String value) {
    int loc = LETTERS.indexOf(value);
    if (loc == -1) {
      throw new IllegalArgumentException("Expected a letter, got " + value + "!");
    }
    return slave.toSignal(loc);
  }

  public String fromSignal(Signal signal) {
    int loc = slave.fromSignal(signal);
    return LETTERS.charAt(loc) + "";
  }

  public int bits() { return slave.bits(); }
}
