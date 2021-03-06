package com.blevinstein.net;

import static com.blevinstein.net.Util.chain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Converter;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

// Represents a neural net using metrices.
//
// Sources:
// http://www.willamette.edu/~gorr/classes/cs449/precond.html
// http://en.wikipedia.org/wiki/Backpropagation

public class NeuralNet {
  //[ y0 ]   [ w00 w01 w02 t0 ][ x0 ]
  //[ y1 ] = [ w10 w11 w12 t1 ][ x1 ]
  //[ -1 ]   [ 0   0   0   1  ][ x2 ]
  //                           [ -1 ]
  // yi = Sum(wij * xj - ti)
  // t = threshold
  
  private List<RealMatrix> layers;

  private static Logger logger = Logger.getLogger("com.blevinstein.net.NeuralNet");

  public int size() {
    return layers.size();
  }
  public RealMatrix getLayer(int i) {
    return layers.get(i).copy();
  }
  public List<RealMatrix> getLayers() {
    // deep copy
    return Lists.transform(layers, RealMatrix::copy);
  }
  public int getInputs() {
    return layers.get(0).getRowDimension() - 1;
  }
  public int getOutputs() {
    return layers.get(layers.size() - 1).getColumnDimension() - 1;
  }

  public static NeuralNet create(int input, int output) {
    return create(ImmutableList.of(input, Math.max(input, output), output));
  }

  public static NeuralNet create(List<Integer> sizes) {
    // Create each matrix
    List<RealMatrix> layers = new ArrayList<>();
    for (Pair<Integer, Integer> dim : chain(sizes)) {
      // I+1,J+1 to include bias term
      RealMatrix layer = newMatrix(dim.getLeft() + 1, dim.getRight() + 1);
      layers.add(affinize(normalize(layer)));
    }
    return new NeuralNet(layers);
  }

  public NeuralNet(List<RealMatrix> layers) {
    this.layers = new ArrayList<>();
    for (int i = 0; i < layers.size(); i++) {
      this.layers.add(layers.get(i));
    }
  }

  // TODO: write a new norm function that ignores the last column
  // public static double normOf(RealMatrix matrix)

  public static RealMatrix newMatrix(int rows, int cols) {
    RealMatrix matrix = MatrixUtils.createRealMatrix(rows, cols);
    for (int i = 0; i < matrix.getRowDimension(); i++) {
      for (int j = 0; j < matrix.getColumnDimension(); j++) {
        matrix.setEntry(i, j, newEntry());
      }
    }
    return matrix;
  }
  // Sets new entries randomly to +-1
  public static double newEntry() {
    return Math.random() < 0.5 ? 1 : -1;
  }

  // Sets the last column of a matrix to [ 0 .. 0 1 ]
  public static RealMatrix affinize(RealMatrix m) {
    RealMatrix result = m.copy();
    for (int i = 0; i < m.getRowDimension(); i++) {
      result.setEntry(i, m.getColumnDimension() - 1, 0.0);
    }
    result.setEntry(m.getRowDimension() - 1, m.getColumnDimension() - 1, 1.0);
    return result;
  }

  // EXPERIMENTAL: adjust matrix so that the [Frobenius] norm = sqrt(J)
  //
  // MATH:
  // when matrix is first created, set a_ij = 1/sqrt(A_j), A_j = fan-in to node j = I
  // norm = Sqrt(Sum(i, Sum(j, a_ij^2)))
  //      = Sqrt(Sum(i, Sum(j, 1/I))) where I = inputs
  //      = Sqrt(I * J * 1/I) = Sqrt(J) where J = outputs
  // scale matrix by k:
  // norm = Sqrt(Sum(i, Sum(j, (k * a_ij)^2)))
  //      = Sqrt(Sum(i, Sum(j, k^2 * a_ij^2)))
  //      = Sqrt(k^2 * Sum(i, Sum(j, a_ij^2)))
  //      = k * Sqrt(Sum(i, Sum(j, a_ij^2))) = k * norm
  // so, to get desired norm X, scale by X/norm
  // note, normalization function will set a_ij = 1/sqrt(A_j) if initialized with a_ij = +1 or -1
  // note, does not account for affine column [ 0 .. 0 1 ]
  public static RealMatrix normalize(RealMatrix m) {
    double desired_norm = Math.sqrt(m.getColumnDimension());
    double current_norm = m.getFrobeniusNorm();
    return m.scalarMultiply(desired_norm / current_norm);
  }

  public List<Signal> propagate(Signal input) {
    checkArgument(input.size() == getInputs(), "Wrong size input supplied.");

    List<Signal> wave = new ArrayList<>();
    wave.add(input);

    Signal current = input;
    for (RealMatrix layer : layers) {
      current = current.sigmoid().apply(layer);
      wave.add(current);
    }

    return wave;
  }

  public Signal apply(Signal input) {
    List<Signal> wave = propagate(input);
    return wave.get(wave.size() - 1);
  }

  // Given weights w_ij
  // x_i = input to node i
  // o_j = S(y_j) = S(sum(x_i * w_ij))
  // dE/dw_ij = dE/do_j * do_j/dy_j * dy_j/dx_i
  //          = (       d_j       ) * x_i
  //     d_j = (t_j - x_j) * d_sigmoid(y_j)      | last layer, given t_j = target at node j
  //           sum(d_j * w_ij) * d_sigmoid(y_j)  | else
  //     x_i = w_ij
  // Can be backpropagated:
  //   d_i-1 = transpose(w) * d_i
  public void backpropagate(Signal input, Signal target) {
    List<Signal> wave = propagate(input);

    checkArgument(target.size() == wave.get(wave.size() - 1).size(),
        "Wrong size target provided to backprop.");

    // Calculate d_j for last layer
    // E = 0.5 * sum(o_j - t_j)^2
    // dE/do = (o - t)
    Signal output = wave.get(wave.size() - 1);
    double[] lastDelta = new double[output.size()];
    for (int i = 0; i < lastDelta.length; i++) {
      lastDelta[i] = output.get(i) - target.get(i);
    }
    
    // Backpropagate d_j
    List<Signal> deltas = new ArrayList<>();
    deltas.add(new Signal(lastDelta));
    for (int i = layers.size() - 1; i >= 0; i--) {
      Signal d_output = deltas.get(deltas.size() - 1);
      Signal d_input = d_output.reverseApply(layers.get(i));
      deltas.add(d_input);
    }
    Collections.reverse(deltas);

    // layerSlope = d_j * x_i
    // weights -= learningRate * layerSlope
    double learningRate = 0.01;
    for (int i = 0; i < layers.size(); i++) {
      RealMatrix layerDelta = wave.get(i).getRealVector().outerProduct(
          deltas.get(i + 1).getRealVector_zeroed()).scalarMultiply(learningRate);
      RealMatrix newLayer = layers.get(i).subtract(layerDelta);
      layers.set(i, affinize(normalize(newLayer)));
    }
    
    checkState(!Double.isNaN(layers.get(0).getEntry(0, 0)), "Matrix has diverged!");
  }

  @Override
  public String toString() {
    return layers.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof NeuralNet) {
      NeuralNet other = (NeuralNet) obj;
      return this.layers.equals(other.layers);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(layers);
  }

  public <X, Y> NetAdapter<X, Y> getAdapter(Converter<X, Signal> cin, Converter<Signal, Y> cout) {
    return new NetAdapter<>(cin, cout);
  }

  public class NetAdapter<X, Y> {
    private Converter<X, Signal> cin;
    private Converter<Signal, Y> cout;

    public NetAdapter(Converter<X, Signal> cin, Converter<Signal, Y> cout) {
      this.cin = cin;
      this.cout = cout;
    }

    public Y netApply(X input) {
      return cout.convert(apply(cin.convert(input)));
    }

    public void netBackpropagate(X input, Y target) {
      backpropagate(cin.convert(input), cout.reverse().convert(target));
    }

    public void drawState(Graphics g, X input, int x, int y, int sx, int sy) {
      Util.drawState(g, NeuralNet.this, cin.convert(input), x, y, sx, sy);
    }

    public void drawState(Graphics g, X input, int x, int y, int sx, int sy, Util.Style mode) {
      Util.drawState(g, NeuralNet.this, cin.convert(input), x, y, sx, sy, mode);
    }
  }

}
