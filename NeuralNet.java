import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

// Represents a neural net using matrices.
//
// Implements Genome so that it can be a member of a Population.

public class NeuralNet implements Genome<NeuralNet> {
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
  
  public NeuralNet(int neurons[]) {
    N = neurons.length-1;
    _weights = new RealMatrix[N];
    for (int k = 0; k < N; k++) {
      // rows and cols have +1 to include bias term
      int rows = neurons[k] + 1;
      int cols = neurons[k+1] + 1;
      _weights[k] = MatrixUtils.createRealMatrix(rows, cols);

      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          // NOTE: max_weight = 1 / sqrt(Ai) where Ai = fan-in to node i
          // http://www.willamette.edu/~gorr/classes/cs449/precond.html
          _weights[k].setEntry(i, j, Util.random() / Math.sqrt(rows));
        }
      }

      setupMatrix(_weights[k]);
    }
  }
  
  public NeuralNet(RealMatrix[] w) {
    _weights = w;
    N = _weights.length;
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
  }

  // sets last column of matrix to [ 0 0 .. 0 1 ]
  private static void setupMatrix(RealMatrix m) {
    int rows = m.getRowDimension();
    int cols = m.getColumnDimension();
    for (int i = 0; i < rows; i++) m.setEntry(i, cols-1, 0.0);
    m.setEntry(rows-1, cols-1, 1.0);
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
  private double d_sigmoid(double x) { double t = Math.tanh(x); return 1 - t*t; }  // dS/dx = 1 - tanh(x)^2
  
  // TODO: allow normalizing input, xi' = (xi - offset) * scalar
  // TODO: allow normalizing output/feedback
  
  // convenience methods to append (wrap) and remove (unwrap)
  // a trailing -1
  public static double[] wrap(double vector[]) {
    double ret[] = Arrays.copyOf(vector, vector.length + 1);
    ret[ret.length-1] = -1;
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
    double outputs[][] = new double[N+1][];
    // NOTE: no sigmoid function applied to initial inputs
    outputs[0] = inputs;
    
    // for each layer
    for(int k = 0; k < N; k++) {
      // store pre-sigmoid values in outputs[][]
      // X * W = Y
      outputs[k+1] = _weights[k].preMultiply(inputs);
      
      // new X = sigmoid(Y) except last term
      inputs = Arrays.stream(outputs[k+1]).map(x -> sigmoid(x)).toArray();
      inputs[inputs.length-1] = -1;
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
    assert targets.length == outputs[N].length :
        "targets should be length " + outputs[N].length + " but is " + targets.length;
    
    // calculate dj, where dE/dwij = dj * xi
    // refer to http://en.wikipedia.org/wiki/Backpropagation
    double delta[][] = new double[N][];
    delta[N-1] = new double[outputs[N].length];
    for (int j = 0; j < delta[N-1].length; j++) {
      delta[N-1][j] = (sigmoid(outputs[N][j]) - targets[j]) * d_sigmoid(outputs[N][j]);
    }
    for (int k = N-2; k >= 0; k--) {
      delta[k] = _weights[k+1].transpose().preMultiply(delta[k+1]);
      // multiply each element by d_sigmoid
      for (int j = 0; j < delta[k].length; j++) {
        delta[k][j] = delta[k][j] * d_sigmoid(outputs[k+1][j]);
      } // last element set to 0 to backprop bias correctly
      delta[k][delta[k].length-1] = 0;
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
      // HACK: makes sure last column is still [ 0 0 .. 1 ]
      setupMatrix(_weights[k]);
      /*
      System.out.println("new matrix " + k);
      pp(_weights[k]);
      */
    }
    
    // HACK: just checks one element, forces crash when matrix diverges
    assert !Double.isNaN(_weights[0].getEntry(0,0));
  }

  // translate into a 3D array for easy serialization
  public double[][][] toDoubles() {
    double arr[][][] = new double[N][][];
    for (int k = 0; k < N; k++) {
      int rows = _weights[k].getRowDimension();
      int cols = _weights[k].getColumnDimension();
      arr[k] = new double[rows][];
      for (int i = 0; i < rows; i++) {
        arr[k][i] = new double[cols];
        for (int j = 0; j < cols; j++) {
          arr[k][i][j] = _weights[k].getEntry(i, j);
        }
      }
    }
    return arr;
  }

  // methods to implement Genome<Self>, allow inclusion in a population
  private final double MAX_MUTATION = 1.0;
  private final double MUTATION_RATE = 0.1;
  public NeuralNet mutate() {
    // TODO: allow architecture (layers and sizes) to mutate
    RealMatrix w[] = new RealMatrix[N];
    for (int k = 0; k < N; k++) {
      int rows = _weights[k].getRowDimension();
      int cols = _weights[k].getColumnDimension();
      w[k] = MatrixUtils.createRealMatrix(rows, cols);
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          double value = _weights[k].getEntry(i, j);
          // with probability MUTATION_RATE..
          if (Math.random() < MUTATION_RATE) {
            // ..alter the weight by less than MAX_MUTATION
            value += Util.random() * MAX_MUTATION;
          }
          w[k].setEntry(i, j, value);
        }
      }
    }
    return new NeuralNet(w);
  }
  public NeuralNet crossover(NeuralNet other) {
    // TODO: handle crossover between varied architectures?
    // TODO: don't just cut between matrices
    RealMatrix otherWeights[] = other.weights();

    assert _weights.length == otherWeights.length;
    
    RealMatrix newWeights[] = new RealMatrix[N];
    // choose a point to cut
    int cross = (int)(Math.random() * (N+1));
    // return this[0..x] :: other[x..N]
    for (int i = 0; i < N; i++) {
      if (i < cross) {
        newWeights[i] = _weights[i].copy();
      } else {
        newWeights[i] = otherWeights[i].copy();
      }
    }
    return new NeuralNet(newWeights);
  }

  // Draws from [x,y] to [x+sx, y+sy].
  // Depicts neurons in layers, connected by synapses.
  // Neurons and synapses colored according to activation.
  // Synapse width corresponds to weight.
  // Arrowed "1.0 => 0.76" on neuron gives the input and output of the sigmoid.
  public void drawState(Graphics g, double inputs[], int x, int y, int sx, int sy) {
    // needed for drawing with Stroke's
    Graphics2D g2 = (Graphics2D)g;

    // perform propagation
    inputs = wrap(inputs);
    double outputs[][] = propagate(inputs);

    // make background gray
    g.setColor(new Color(0.5f, 0.5f, 0.5f));
    g.fillRect(x, y, sx, sy);

    // calculate spacing [dx, dy]
    // calculate neuron size (diameter)
    int maxNeurons = _weights[0].getRowDimension();
    for (int i = 0; i < N; i++) {
      int neurons = _weights[i].getColumnDimension();
      if (neurons > maxNeurons) maxNeurons = neurons;
    }
    double dy = sy / maxNeurons;
    double dx = sx / outputs.length;
    // NOTE: 0.5 = arbitrary constant less than 1.0, for spacing
    int diameter = (int)(Math.min(dx, dy) * 0.5);

    // draw neurons
    for (int i = 0; i < outputs.length; i++) { // each layer
      for (int j = 0; j < outputs[i].length; j++) { // each neuron
        // skip the last bias element
        if (i == outputs.length-1 && j == outputs[i].length-1) continue;

        // calculate grayscale color to use
        /*
        // HACK: outputs[][] holds pre-sigmoid values, except output[0] = inputs
        float value = (i == 0) ?              // if inputs
                      outputs[i][j] :         // no sigmoid
                      sigmoid(outputs[i][j]); // else sigmoid
        */
        float value = (float)(sigmoid(outputs[i][j]) * 0.5 + 0.5);
        Color gray = new Color(value, value, value);
        Color tgray = new Color(value, value, value, 0.5f); // translucent gray
        Color contrast = value > 0.5 ? Color.BLACK : Color.WHITE;

        // draw outgoing synapses
        if (i+1 < outputs.length) { // except for last row
          for (int m = 0; m < outputs[i+1].length-1; m++) { // each synapse
            double weight = _weights[i].getEntry(j, m);

            if (weight == 0) continue; // skip synapses which aren't connected

            double mag = Math.abs(weight);
            // NOTE: 0.5 = arbitrary constant less than 1
            int width = (int)(mag / (1 + mag) * diameter * 0.5);
            
            // draw synapse
            g2.setColor(tgray);
            g2.setStroke(new BasicStroke(width));
            g2.drawLine((int)(x + dx*(0.5 + i)),
                        (int)(y + dy*(0.5 + j)),
                        (int)(x + dx*(1.5 + i)),
                        (int)(y + dy*(0.5 + m)));
            g2.setStroke(new BasicStroke(1.0f));

            // display the synapse weight
            g2.setColor(contrast);
            // t is used to determine placement along the synapse, to avoid
            //   labels overlapping. For simple midpoint text, set t=0.5.
            double t = (j + 1.0) / (outputs[i].length + 1.0);
            Util.placeText(g, Util.CENTER, String.format("%.2f", weight),
                           (int)(x + dx*(i + 0.5 + t)),
                           (int)(y + dy*(0.5 + j + (m-j)*t)));
          }
        }

        // diameter is halved for bias nodes
        int d = j == outputs[i].length-1 ?
                diameter / 2 :
                diameter;
        // circle is centered on square [i,j] with given side length, diameter d
        g.setColor(gray);
        g.fillOval((int)(x + dx*(0.5 + i) - d/2),
                   (int)(y + dy*(0.5 + j) - d/2),
                   d, d);
        // draw border
        g.setColor(Color.BLACK);
        g.drawOval((int)(x + dx*(0.5 + i) - d/2),
                   (int)(y + dy*(0.5 + j) - d/2),
                   d, d);

        // display the neuron's pre- and post-sigmoid values
        // NOTE: inputs (outputs[0]) and biases (outputs[i].last) don't get sigmoided
        g.setColor(contrast);
        String str = i == 0 || j == outputs[i].length-1 ?
                     String.format("%.2f", outputs[i][j]) :
                     String.format("%.2f => %.2f", outputs[i][j], sigmoid(outputs[i][j]));
        Util.placeText(g, Util.CENTER, str,
                       (int)(x + dx*(0.5 + i)),
                       (int)(y + dy*(0.5 + j)));
      }
    }
  }
}
