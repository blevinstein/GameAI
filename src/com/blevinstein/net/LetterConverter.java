package com.blevinstein.net;

import com.google.common.base.Converter;

public class LetterConverter extends Converter<String, Integer> {

  public static String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  public Integer doForward(String value) {
    int loc = LETTERS.indexOf(value);
    if (loc == -1) {
      throw new IllegalArgumentException("Expected a letter, got " + value + "!");
    }
    return loc;
  }

  public String doBackward(Integer i) {
    return LETTERS.charAt(i) + "";
  }
}
