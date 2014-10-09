package com.blevinstein.net;

public class LetterConverter implements Converter<String> {
  
  public static String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  // does all the heavy lifting
  private EnumConverter slave = new EnumConverter(26);

  public double[] toDoubles(String value) {
    int loc = LETTERS.indexOf(value);
    if (loc == -1)
      throw new IllegalArgumentException("Expected a letter, got " + value + "!");
    return slave.toDoubles(loc);
  }

  public String fromDoubles(double doubles[]) {
    int loc = slave.fromDoubles(doubles);
    return LETTERS.charAt(loc) + "";
  }
  
  public int bits() { return slave.bits(); }
}
