package com.blevinstein.net;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

// Represents an input signal as a vector with an implicit trailing -1

// TODO: add tests

public class Signal {
  private double[] vector;

  public Signal(List<Double> list) {
    vector = new double[list.size()];
    for (int i = 0; i < list.size(); i++) {
      vector[i] = list.get(i);
    }
  }

  public Signal(double[] vector) {
    this.vector = vector;
  }

  public int size() {
    return vector.length;
  }
  public double get(int i) {
    if (i < 0 || i >= vector.length) {
      throw new IllegalArgumentException(String.format("Index %d out of bounds", i));
    }
    return vector[i];
  }
  public double[] getVector() {
    return Arrays.copyOf(vector, vector.length);
  }
  public RealVector getRealVector() {
    return new ArrayRealVector(wrap(vector));
  }

  public Signal sigmoid() {
    double[] newVector = new double[vector.length];
    for (int i = 0; i < newVector.length - 1; i++) {
      newVector[i] = sigmoid(vector[i]);
    }
    return new Signal(newVector);
  }

  public Signal apply(RealMatrix matrix) {
    return new Signal(unwrap(matrix.preMultiply(wrap(vector))));
  }

  public Signal reverseApply(RealMatrix matrix) {
    return apply(matrix.transpose());
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
      throw new IllegalStateException("Vector cannot be unwrapped, no trailing -1");
    }
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

  @Override
  public boolean equals(Object object) {
    if (object instanceof Signal) {
      Signal other = (Signal) object;
      return Arrays.equals(vector, other.getVector());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(vector);
  }
}
