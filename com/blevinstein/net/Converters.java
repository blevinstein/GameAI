package com.blevinstein.net;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Static convenience methods for Converter types.

public class Converters {
  private static Map<String, Converter<?>> _map = new HashMap<>();

  private static boolean ready = false;
  private static void init() {
    if (ready) { return; }
    register(new BinaryConverter(), Boolean.class);
    ready = true;
  }

  @SuppressWarnings("unchecked")
  public static <T> Converter<T> get(String className) {
    init(); // HACK to register converters before use

    try {
      Converter<T> conv = (Converter<T>)_map.get(className);
      return conv;
    } catch (NullPointerException e) {
      System.err.println("Could not retrieve converter of type " + className + "!");
      return null;
    }
  }

  public static <T> void register(Converter<T> converter, Class<T> klass) {
    _map.put(klass.getName(), converter);
    System.out.println("Registered converter for " + klass.getName() + ".");
  }

  // create a homogenous ListConverter
  public static ListConverter list(String className, int count) {
    return new ListConverter(Collections.nCopies(count, get(className)));
  }
  // create a heterogenous ListConverter
  public static ListConverter list(List<String> classNames) {
    return new ListConverter(
             classNames.stream()
             .map(cn -> get(cn))
             .collect(Collectors.toList()));
  }

  // create an ArrayConverter
  public static <T> ArrayConverter<T> array(Class<T> klass, int count) {
    return new ArrayConverter<T>(klass, get(klass.getName()), count);
  }
}
