package com.blevinstein.net;

import com.blevinstein.util.Util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.QuadCurve2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

// Represents a neural net using matrices.

public class NeuralNet {
  //[ y0 ]   [ w00 w01 w02 t0 ][ x0 ]
  //[ y1 ] = [ w10 w11 w12 t1 ][ x1 ]
  //[ -1 ]   [ 0   0   0   1  ][ x2 ]
  //                           [ -1 ]
  // yi = Sum(wij * xj - ti)
  // t = threshold

  private double LEARNING_RATE = 0.01;

  private RealMatrix _weights[];
  public RealMatrix[] weights() { return _weights; }

  private final int N; // numbers of layers, for convenience

  // TODO: add capability to normalize network output
  // TODO: make NetAdapter the primary interface?

  public NeuralNet(int inputs, int outputs) {
    this(new int[] {inputs, (int)Math.round((inputs + outputs) / 2.0), outputs});
  }

  public NeuralNet(int neurons[]) {
    N = neurons.length - 1;
    _weights = new RealMatrix[N];
    for (int k = 0; k < N; k++) {
      // rows and cols have +1 to include bias term
      int rows = neurons[k] + 1;
      int cols = neurons[k + 1] + 1;
      _weights[k] = MatrixUtils.createRealMatrix(rows, cols);

      // initialize weights
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          // NOTE: initial weight = +-1 / sqrt(Ai) where Ai = fan-in to node i
          // http://www.willamette.edu/~gorr/classes/cs449/precond.html
          // _weights[k].setEntry(i, j, Util.random() / Math.sqrt(rows));
          _weights[k].setEntry(i, j, (Math.random() < 0.5 ? 1 : -1));
        }
      }
    }

    // scale matrices and set last column
    normalize();
  }
  public NeuralNet(RealMatrix[] w) {
    _weights = w;
    N = _weights.length;

    normalize();
  }
  public NeuralNet(double[][][] arr) {
    N = arr.length;
    _weights = new RealMatrix[N];
    for (int k = 0; k < N; k++) {
      _weights[k] = new Array2DRowRealMatrix(arr[k].length, arr[k][0].length);
      for (int i = 0; i < arr[k].length; i++) {
        for (int j = 0; j < arr[k][i].length; j++) {
          _weights[k].setEntry(i, j, arr[k][i][j]);
        }
      }
    }

    normalize();
  }

  // sets last column of matrix to [ 0 0 .. 0 1 ]
  private static void setupMatrix(RealMatrix m) {
    int rows = m.getRowDimension();
    int cols = m.getColumnDimension();
    for (int i = 0; i < rows; i++) { m.setEntry(i, cols - 1, 0.0); }
    m.setEntry(rows - 1, cols - 1, 1.0);
  }

  // NOTE: use tanh instead of 1/(1+e^(-u)) as suggested by
  // http://www.willamette.edu/~gorr/classes/cs449/precond.html
  /*
  double sigmoid(double x) { return 1 / (1 + Math.exp(-x)); }
  double d_sigmoid(double x) {
    // NOTE: sacrificing pretty syntax to avoid calling sigmoid() twice
    double s = sigmoid(x);
    return s * (1 - s);
  }
  */
  private double sigmoid(double x) { return Math.tanh(x); }                        // S = tanh(x)
  private double d_sigmoid(double x) { double t = Math.tanh(x); return 1 - t * t; } // dS/dx = 1 - tanh(x)^2

  // convenience methods to append (wrap) and remove (unwrap)
  // a trailing -1
  public static double[] wrap(double vector[]) {
    double ret[] = Arrays.copyOf(vector, vector.length + 1);
    ret[ret.length - 1] = -1;
    return ret;
  }
  public static double[] unwrap(double vector[]) {
    return Arrays.copyOf(vector, vector.length - 1);
  }

  // give outputs for a set of inputs
  public double[] process(double[] inputs) {
    // append -1, e.g. [ x0 x1 x2 -1 ]
    inputs = wrap(inputs);

    double layers[][] = propagate(inputs);
    double output[] = layers[layers.length - 1];

    // remove extra -1, [ y0 y1 -1 ]
    // NOTE: output is sigmoided, only allows binary (not scalar) outputs
    return Arrays.stream(unwrap(output)).map(x -> sigmoid(x)).toArray();
  }

  // propagate a series of values through the network
  // RETURN intermediate layers, pre-sigmoid
  // assumes that inputs have been wrapped (-1 appended)
  public double[][] propagate(double[] inputs) {
    // propagate across each layer, saving pre-sigmoid output
    // length+1 to include input layer
    double outputs[][] = new double[N + 1][];
    // NOTE: no sigmoid function applied to initial inputs
    outputs[0] = inputs;

    // for each layer
    for (int k = 0; k < N; k++) {
      // store pre-sigmoid values in outputs[][]
      // X * W = Y
      outputs[k + 1] = _weights[k].preMultiply(inputs);

      // new X = sigmoid(Y) except last term
      inputs = Arrays.stream(outputs[k + 1]).map(x -> sigmoid(x)).toArray();
      inputs[inputs.length - 1] = -1;
    }
    return outputs;
  }

  // backpropagate a correct value back through the network
  // returns the error E, calculated as Sum[(xi-ti)^2] where ti are targets
  // assumes that inputs and targets are NOT pre-wrapped (-1 appended)
  public void backpropagate(double[] inputs, double[] targets) {
    // append extra -1
    inputs = wrap(inputs);
    targets = wrap(targets);

    double outputs[][] = propagate(inputs);

    // check length of targets
    if (targets.length != outputs[N].length) {
      throw new RuntimeException("Wrong target length!");
    }

    // calculate dj, where dE/dwij = dj * xi
    // refer to http://en.wikipedia.org/wiki/Backpropagation
    double delta[][] = new double[N][];
    delta[N - 1] = new double[outputs[N].length];
    for (int j = 0; j < delta[N - 1].length; j++) {
      delta[N - 1][j] = (sigmoid(outputs[N][j]) - targets[j]) * d_sigmoid(outputs[N][j]);
    }
    for (int k = N - 2; k >= 0; k--) {
      delta[k] = _weights[k + 1].transpose().preMultiply(delta[k + 1]);
      // multiply each element by d_sigmoid
      for (int j = 0; j < delta[k].length; j++) {
        delta[k][j] = delta[k][j] * d_sigmoid(outputs[k + 1][j]);
      } // last element set to 0 to backprop bias correctly
      delta[k][delta[k].length - 1] = 0;
    }

    // update weights
    for (int k = 0; k < N; k++) { // for each layer
      // TODO: set local learning rates
      // http://www.willamette.edu/~gorr/classes/cs449/precond.html

      // layerSlope = delta ** outputs, represents dE/dwij(** = outer product)
      // W -= learning_rate * layerSlope
      RealMatrix layerSlope =
        new ArrayRealVector(outputs[k]).outerProduct(
        new ArrayRealVector(delta[k]));
      _weights[k] = _weights[k].subtract(layerSlope.scalarMultiply(LEARNING_RATE));
    }

    // scale matrices and set last column
    normalize();

    // HACK: just checks one element, forces crash when matrix diverges
    if (Double.isNaN(_weights[0].getEntry(0, 0))) {
      throw new RuntimeException("Matrix has diverged!");
    }
  }

  // EXPERIMENTAL: adjust matrix so that the [Frobenius] norm stays constant
  //
  // This came from the observation that all of the weights in a given matrix
  // may approach zero, essentially severing the connection between two layers
  // in the network.
  //
  // I am concerned that this might have negative consequences in the opposite
  // situation, when the weights are large.
  //
  // MATH:
  // when matrix is first created:
  // norm = Sqrt(Sum(i, Sum(j, a_ij^2)))
  //      = Sqrt(Sum(i, Sum(j, 1/I))) where I = inputs, i.e. fan-in
  //      = Sqrt(I * J * 1/I) = Sqrt(J) where J = outputs
  // scale matrix by k:
  // norm = Sqrt(Sum(i, Sum(j, (k * a_ij)^2)))
  //      = Sqrt(Sum(i, Sum(j, k^2 * a_ij^2)))
  //      = Sqrt(k^2 * Sum(i, Sum(j, a_ij^2)))
  //      = k * Sqrt(Sum(i, Sum(j, a_ij^2))) = k * norm
  // so, to get desired norm X, scale by X/norm
  private void normalize() {
    for (int k = 0; k < _weights.length; k++) {
      setupMatrix(_weights[k]);
      double desired_norm = Math.sqrt(_weights[k].getColumnDimension());
      double current_norm = _weights[k].getFrobeniusNorm();
      _weights[k] = _weights[k].scalarMultiply(desired_norm / current_norm);
      setupMatrix(_weights[k]);
    }
  }

  // Draws from [x,y] to [x+sx, y+sy].
  // Depicts neurons in layers, connected by synapses.
  // Neurons and synapses colored according to activation.
  // Synapse width corresponds to weight.
  // Arrowed "1.0 => 0.76" on neuron gives the input and output of the sigmoid.
  // mode determines method of drawing connections, MAG or SVD.
  public void drawState(Graphics g, double inputs[],
                        int x, int y, int sx, int sy) {
    drawState(g, inputs, x, y, sx, sy, MAG);
  }
  public static final int MAG = 0, SVD = 1;
  public void drawState(Graphics g, double inputs[],
                        int x, int y, int sx, int sy, int mode) {
    // needed for drawing with Stroke's
    Graphics2D g2 = (Graphics2D)g;

    // perform propagation
    inputs = wrap(inputs);
    double outputs[][] = propagate(inputs);

    // make background gray
    g.setColor(new Color(0.5f, 0.5f, 0.5f));
    g.fillRect(x, y, sx, sy);

    // calculate spacing (grid)
    // calculate neuron size (diameter)
    int maxNeurons = _weights[0].getRowDimension();
    for (int i = 0; i < N; i++) {
      int neurons = _weights[i].getColumnDimension();
      if (neurons > maxNeurons) { maxNeurons = neurons; }
    }
    Vector2D grid = new Vector2D(sx / outputs.length, sy / maxNeurons);
    // NOTE: 0.5 = arbitrary constant less than 1.0, for spacing
    double diameter = Math.min(grid.getX(), grid.getY()) * 0.5;

    // set font
    g.setFont(new Font("Arial", Font.PLAIN, (int)(diameter / 8)));

    // draw neurons
    for (int i = 0; i < outputs.length; i++) { // each layer
      for (int j = 0; j < outputs[i].length; j++) { // each neuron
        // skip the last bias element
        if (i == outputs.length - 1 && j == outputs[i].length - 1) { continue; }

        // calculate grayscale color to use
        float value = (float)(sigmoid(outputs[i][j]) * 0.5 + 0.5);
        Color gray = new Color(value, value, value);
        Color tgray = new Color(value, value, value, 0.5f); // translucent gray
        Color contrast = value > 0.5 ? Color.BLACK : Color.WHITE;

        // draw synapses, layer i, outgoing from neuron j...
        if (i + 1 < outputs.length) { // except for last row

          // SVD-related preprocessing
          SingularValueDecomposition svd = new SingularValueDecomposition(
            _weights[i]);
          int p = svd.getRank();
          RealMatrix a[] = new RealMatrix[p];
          if (mode == SVD) {
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
          for (int m = 0; m < outputs[i + 1].length - 1; m++) {

            int widths[];
            Color colors[];

            double weight = _weights[i].getEntry(j, m);

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
              Vector2D from = Util.multiply(grid, new Vector2D(0.5 + i, 0.5 + j))
                              .add(origin);
              Vector2D to = Util.multiply(grid, new Vector2D(1.5 + i, 0.5 + m))
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
              double t = (j + 1.0) / (outputs[i].length + 1.0);
              Util.placeText(g, Util.CENTER, String.format("%.2f", weight),
                             (int)(x + grid.getX() * (i + 0.5 + t)),
                             (int)(y + grid.getY() * (0.5 + j + (m - j) * t)));
            }

          }
        }

        // diameter is halved for bias nodes
        double d = j == outputs[i].length - 1 ?
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
        // NOTE: inputs (outputs[0]) and biases (outputs[i].last) don't get sigmoided
        g.setColor(contrast);
        String str = i == 0 || j == outputs[i].length - 1 ?
                     String.format("%.2f", outputs[i][j]) :
                     String.format("%.2f => %.2f", outputs[i][j], sigmoid(outputs[i][j]));
        Util.placeText(g, Util.CENTER, str,
                       (int)(x + grid.getX() * (0.5 + i)),
                       (int)(y + grid.getY() * (0.5 + j)));
      }
    }
  }
}
