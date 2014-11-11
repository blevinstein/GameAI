package com.blevinstein.net;

import static com.blevinstein.net.Util.chain;
import static com.blevinstein.util.Util.multiply;
import static com.blevinstein.util.Util.placeText;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.blevinstein.util.Util.Align;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.QuadCurve2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

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
  
  // TODO: add capability to normalize network output
  // TODO: make NetAdapter the primary interface?
  
  private List<RealMatrix> layers;

  public int size() {
    return layers.size();
  }
  public RealMatrix getLayer(int i) {
    return layers.get(i).copy();
  }
  public List<RealMatrix> getLayers() {
    // deep copy
    return Lists.transform(layers, layer -> layer.copy());
  }
  public int getInputs() {
    return layers.get(0).getRowDimension() - 1;
  }
  public int getOutputs() {
    return layers.get(layers.size() - 1).getColumnDimension() - 1;
  }

  public static NeuralNet create(List<Integer> sizes) {
    // Create each matrix
    System.out.println("Create net size " + sizes);
    List<RealMatrix> layers = new ArrayList<>();
    for (Pair<Integer, Integer> dim : chain(sizes)) {
      System.out.println("Create layer size " + dim.getLeft() + "," + dim.getRight());
      // I+1,J+1 to include bias term
      RealMatrix layer = MatrixUtils.createRealMatrix(dim.getLeft() + 1, dim.getRight() + 1);
      for (int i = 0; i < layer.getRowDimension(); i++) {
        for (int j = 0; j < layer.getColumnDimension(); j++) {
          layer.setEntry(i, j, newEntry());
        }
      }
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

  // EXPERIMENTAL: adjust matrix so that the [Frobenius] norm stays constant
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
    checkArgument(input.size() + 1 == getInputs(), "Wrong size input supplied.");

    Signal current = input;

    List<Signal> wave = new ArrayList<>();
    wave.add(current);

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
      RealMatrix layerSlope = new ArrayRealVector(wave.get(i).getVector()).outerProduct(
          new ArrayRealVector(deltas.get(i).getVector()));
      RealMatrix newLayer = layers.get(i).subtract(layerSlope.scalarMultiply(learningRate));
      layers.set(i, affinize(normalize(newLayer)));
    }
    
    checkState(!Double.isNaN(layers.get(0).getEntry(0, 0)), "Matrix has diverged!");
  }

  // Draws from [x,y] to [x+sx, y+sy].
  // Depicts neurons in layers, connected by synapses.
  // Neurons and synapses colored according to activation.
  // Synapse width corresponds to weight.
  // Arrowed "1.0 => 0.76" on neuron gives the input and output of the sigmoid.
  // mode determines method of drawing connections, MAG or SVD.
  // TODO: refactor this awful crap into another file
  public void drawState(Graphics g, Signal input,
                        int x, int y, int sx, int sy) {
    drawState(g, input, x, y, sx, sy, Style.MAG);
  }
  public static enum Style {
    MAG, SVD
  }
  public void drawState(Graphics g, Signal input,
                        int x, int y, int sx, int sy, Style mode) {
    // needed for drawing with Stroke's
    Graphics2D g2 = (Graphics2D)g;

    // perform propagation
    List<Signal> wave = propagate(input);

    // make background gray
    g.setColor(new Color(0.5f, 0.5f, 0.5f));
    g.fillRect(x, y, sx, sy);

    // calculate spacing (grid)
    // calculate neuron size (diameter)
    int maxNeurons = layers.get(0).getRowDimension();
    for (int i = 0; i < layers.size(); i++) {
      int neurons = layers.get(i).getColumnDimension();
      if (neurons > maxNeurons) { maxNeurons = neurons; }
    }
    Vector2D grid = new Vector2D(sx / wave.size(), sy / maxNeurons);
    // NOTE: 0.5 = arbitrary constant less than 1.0, for spacing
    double diameter = Math.min(grid.getX(), grid.getY()) * 0.5;

    // set font
    g.setFont(new Font("Arial", Font.PLAIN, (int)(diameter / 8)));

    // draw neurons
    for (int i = 0; i < wave.size(); i++) { // each layer
      for (int j = 0; j < wave.get(i).size(); j++) { // each neuron
        // skip the last bias element
        if (i == wave.size() - 1 && j == wave.get(i).size() - 1) { continue; }

        // calculate grayscale color to use
        float value = (float)(Signal.sigmoid(wave.get(i).get(j)) * 0.5 + 0.5);
        Color gray = new Color(value, value, value);
        Color tgray = new Color(value, value, value, 0.5f); // translucent gray
        Color contrast = value > 0.5 ? Color.BLACK : Color.WHITE;

        // draw synapses, layer i, outgoing from neuron j...
        if (i + 1 < wave.size()) { // except for last row

          // SVD-related preprocessing
          SingularValueDecomposition svd = new SingularValueDecomposition(
            layers.get(i));
          int p = svd.getRank();
          RealMatrix a[] = new RealMatrix[p];
          if (mode == Style.SVD) {
            RealMatrix u = svd.getU();
            RealMatrix v = svd.getVT();
            for (int k = 0; k < p; k++) {
              RealMatrix s = new DiagonalMatrix(p);
              s.setEntry(k, k, svd.getSingularValues()[k]);
              // IDEA: set to 1.0?
              //s.setEntry(k, k, 1.0);
              a[k] = u.multiply(s).multiply(v);
            }
          }

          // ...to neuron m
          for (int m = 0; m < wave.get(i + 1).size() - 1; m++) {

            int widths[];
            Color colors[];

            double weight = layers.get(i).getEntry(j, m);

            // perform mode-specific processing
            switch (mode) {
              case MAG:
                if (Math.abs(weight) < 0.1) { continue; } // skip synapses which aren't connected

                double mag = Math.abs(weight);

                widths = new int[1];
                // NOTE: 0.5 = arbitrary constant less than 1
                widths[0] = (int)(mag / (1 + mag) * diameter * 0.5);

                colors = new Color[] { tgray };

                break;
              case SVD:
                double[] rgb = new double[p < 3 ? p : 3];
                for (int k = 0; k < rgb.length; k++) {
                  rgb[k] = Math.abs(a[k].getEntry(j, m));
                }

                widths = new int[rgb.length];
                // NOTE: 0.5 = arbitrary constant less than 1
                for (int w = 0; w < widths.length; w++) {
                  widths[w] = (int)(rgb[w] / (1 + rgb[w]) * diameter * 0.5);
                }

                colors = new Color[] { new Color(1f, 0, 0, 0.5f),
                         new Color(0, 1f, 0, 0.5f),
                         new Color(0, 0, 1f, 0.5f)
                };

                break;
              default:
                throw new UnsupportedOperationException("Invalid mode.");
            }

            // for each connection
            for (int w = 0; w < widths.length; w++) {
              Vector2D origin = new Vector2D(x, y);
              Vector2D from = multiply(grid, new Vector2D(0.5 + i, 0.5 + j))
                              .add(origin);
              Vector2D to = multiply(grid, new Vector2D(1.5 + i, 0.5 + m))
                            .add(origin);
              // (x, y) perpendicular to (x, -y) or (-x, y)
              // both should be normalized
              Vector2D neuronVector = to.subtract(from);
              Vector2D perpVector =
                new Vector2D(neuronVector.getY(), -neuronVector.getX());
              Vector2D mid = from.add(to).scalarMultiply(0.5);
              // NOTE: 0.2 is a constant close to 0.0
              Vector2D offset = perpVector
                                .scalarMultiply(0.2 * (w * 2.0 / (widths.length - 1) - 1.0))
                                .add(mid);
              g2.setColor(colors[w]);
              g2.setStroke(new BasicStroke(widths[w]));
              // g2.drawLine((int)from.getX(), (int)from.getY(),
              //             (int)to.getX(),   (int)to.getY());
              QuadCurve2D curve = new QuadCurve2D.Double();
              curve.setCurve(from.getX(), from.getY(),
                             offset.getX(), offset.getY(),
                             to.getX(), to.getY());
              g2.draw(curve);
              g2.setStroke(new BasicStroke(1.0f));

              // display the synapse weight
              g2.setColor(contrast);
              // t is used to determine placement along the synapse, to avoid
              //   labels overlapping. For simple midpoint text, set t=0.5.
              double t = (j + 1.0) / (wave.get(i).size() + 1.0);
              placeText(g, Align.CENTER, String.format("%.2f", weight),
                             (int)(x + grid.getX() * (i + 0.5 + t)),
                             (int)(y + grid.getY() * (0.5 + j + (m - j) * t)));
            }

          }
        }

        // diameter is halved for bias nodes
        double d = j == wave.get(i).size() - 1 ?
                   diameter / 2 :
                   diameter;
        // circle is centered on square [i,j] with given side length, diameter d
        g.setColor(gray);
        g.fillOval((int)(x + grid.getX() * (0.5 + i) - d / 2),
                   (int)(y + grid.getY() * (0.5 + j) - d / 2),
                   (int)d, (int)d);
        // draw border
        g.setColor(Color.BLACK);
        g.drawOval((int)(x + grid.getX() * (0.5 + i) - d / 2),
                   (int)(y + grid.getY() * (0.5 + j) - d / 2),
                   (int)d, (int)d);

        // display the neuron's pre- and post-sigmoid values
        // NOTE: biases don't get sigmoided
        g.setColor(contrast);
        String str = j == wave.get(i).size() - 1 ?
                     String.format("%.2f", wave.get(i).get(j)) :
                     String.format("%.2f => %.2f", wave.get(i).get(j),
                         Signal.sigmoid(wave.get(i).get(j)));
        placeText(g, Align.CENTER, str,
                       (int)(x + grid.getX() * (0.5 + i)),
                       (int)(y + grid.getY() * (0.5 + j)));
      }
    }
  }
}
