package com.blevinstein.net;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import static com.google.common.base.Preconditions.checkArgument;

// Represents an input signal as a vector with an implicit trailing -1

// TODO: add tests

public class Signal {
  private List<Double> vector;

  private static Logger logger = Logger.getLogger("com.blevinstein.net.Signal");

  public Signal(List<Double> list) {
    vector = ImmutableList.copyOf(list);
  }

  public Signal(double[] doubles) {
    vector = new ArrayList<>();
    for (double d : doubles) {
      vector.add(d);
    }
  }

  public int size() {
    return vector.size();
  }
  public double get(int i) {
    if (i < 0 || i > vector.size()) {
      throw new IllegalArgumentException(String.format("Index %d out of bounds", i));
    }
    if (i == vector.size()) {
      return -1;
    }
    return vector.get(i);
  }
  public double[] getVector() {
    double[] result = new double[vector.size()];
    for (int i = 0; i < vector.size(); i++) {
      result[i] = vector.get(i);
    }
    return result;
  }
  public List<Double> getVectorList() {
    List<Double> list = new ArrayList<>();
    for (double d : vector) {
      list.add(d);
    }
    return list;
  }
  public RealVector getRealVector() {
    return new ArrayRealVector(wrap(getVector()));
  }
  public RealVector getRealVector_zeroed() {
    double[] wrapped = wrap(getVector());
    wrapped[wrapped.length - 1] = 0.0;
    return new ArrayRealVector(wrapped);
  }

  public Signal sigmoid() {
    return new Signal(Lists.transform(vector, x -> sigmoid(x)));
  }

  // NOTE: matrix should be affine
  public Signal apply(RealMatrix matrix) {
    return new Signal(unwrap(matrix.preMultiply(wrap(getVector()))));
  }

  public Signal reverseApply(RealMatrix matrix) {
    // NOTE: unchecked unwrap because transpose of affine matrix is not affine
    return new Signal(unwrap_nocheck(matrix.transpose().preMultiply(wrap(getVector()))));
  }

  // convenience methods to append (wrap) and remove (unwrap)
  // a trailing -1
  public static double[] wrap(double[] vector) {
    double ret[] = Arrays.copyOf(vector, vector.length + 1);
    ret[ret.length - 1] = -1;
    return ret;
  }
  public static double[] unwrap(double[] vector) {
    if (vector[vector.length - 1] != -1) {
      logger.warning(String.format("Vector cannot be safely unwrapped, no trailing -1: %s",
          Arrays.toString(vector)));
      throw new IllegalArgumentException();
    }
    return Arrays.copyOf(vector, vector.length - 1);
  }
  public static double[] unwrap_nocheck(double[] vector) {
    return Arrays.copyOf(vector, vector.length - 1);
  }

  // S = tanh(x)
  public static double sigmoid(double x) {
    return Math.tanh(x);
  }
  // dS/dx = 1 - tanh(x)**2
  public static double d_sigmoid(double x) {
    double t = Math.tanh(x);
    return 1 - t * t;
  }

  public double dist(Signal other) {
    double total = 0.0;
    checkArgument(vector.size() == other.vector.size(), "Wrong size vector supplied");
    for (int i = 0; i < vector.size(); i++) {
      total += Math.pow(vector.get(i) - other.vector.get(i), 2);
    }
    return total;
  }

  @Override
  public String toString() {
    return vector.toString();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Signal) {
      Signal other = (Signal) object;
      return this.vector.equals(other.vector);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(vector);
  }
}
