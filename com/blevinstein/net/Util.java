package com.blevinstein.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Util {
  /**
   * chain({0, 1, 2, 3}) -> {(0, 1), (1, 2), (2, 3)}
   */
  public static <T> List<Pair<T,T>> chain(List<T> list) {
    List<Pair<T,T>> result = new ArrayList<>();
    for (int i = 0; i < list.size() - 1; i++) {
      result.add(Pair.of(list.get(i), list.get(i+1)));
    }
    return result;
  }
}
