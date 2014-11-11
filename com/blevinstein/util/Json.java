package com.blevinstein.util;

import com.blevinstein.genetics.Population;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSerializationContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

// Used for static code related to Json output.
//
// Objects can be serialized or deserialized using save and load. To allow
// handling of NewClass, implement a NewClassSerializer and/or
// NewClassDeserializer and register them in init.

public class Json {
  static Gson gson = init();

  private static Gson init() {
    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
    return builder.create();
  }

  public static <T> void save(T object, String fname) {
    String type = object.getClass().getName();
    try {
      FileUtils.writeStringToFile(new File(fname), gson.toJson(object));
      System.out.println("Saved a " + type + " to " + fname + ".");
    } catch (IOException e) {
      System.err.println("Could not save a " + type + " to " + fname + "!");
    }
  }

  public static <T> T load(String fname, Class<T> klass) {
    String type = klass.getName();
    try {
      T object = gson.fromJson(FileUtils.readFileToString(new File(fname)),
                               klass);
      System.out.println("Loaded a " + type + " from " + fname + ".");
      return object;
    } catch (IOException e) {
      System.err.println("Could not load a " + type + " from " + fname + "!");
      return null;
    }
  }
}
