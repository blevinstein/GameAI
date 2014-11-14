package com.blevinstein.xade;

import static java.lang.Math.PI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

// TODO(blevinstein): rename or refactor

public class Misc {
  public static List<Color> chooseColors(int n) {
    double offset = (float)(Math.random() * 2 * PI);
    List<Color> colors = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      colors.add(Color.getHSBColor((float)(offset + 2 * PI * i / n), 1f, 1f));
    }
    return colors;
  }
}
