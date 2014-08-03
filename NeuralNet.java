import java.util.Arrays;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

// represents a neural network using matrices
public class NeuralNet {
  //[ y0 ]   [ w00 w01 w02 t0 ][ x0 ]
  //[ y1 ] = [ w10 w11 w12 t1 ][ x1 ]
  //[ -1 ]   [ 0   0   0   1  ][ x2 ]
  //                           [ -1 ]
  // yi = Sum(wij * xj - ti)
  // t = threshold
  
  private double LEARNING_RATE = 1;
  
  private RealMatrix weights[];
  public RealMatrix[] getWeights() { return weights; }
  
  private final int N; // numbers of layers, for convenience
  
  public NeuralNet(int neurons[]) {
    N = neurons.length-1;
    weights = new RealMatrix[N];
    for (int i = 0; i < N; i++) {
      weights[i] = newMatrix(neurons[i], neurons[i+1]);
    }
  }
  
  public NeuralNet(RealMatrix[] w) {
    weights = w;
    N = weights.length;
  }
  
  // creates an I x O matrix
  public RealMatrix newMatrix(int inputs, int outputs) {
    RealMatrix mat = new Array2DRowRealMatrix(inputs+1, outputs+1);
    // initialize matrix values between -1 and 1 times max_weight
    for (int i = 0; i < inputs + 1; i++) {
      for (int j = 0; j < outputs; j++) {
        // TODO: max_weight = 1 / sqrt(Ai) where Ai = fan-in to node i
        // mat.setEntry(i, j, (Math.random() * 2 - 1) * (1 / Math.sqrt(inputs)));
        // http://www.willamette.edu/~gorr/classes/cs449/precond.html
        mat.setEntry(i, j, Math.random() - 0.5);
      }
    }
    // last col is [ 0 0 ..  1 ]
    for (int i = 0; i < inputs; i++) {
      mat.setEntry(i, outputs, 0);
    }
    mat.setEntry(inputs, outputs, 1);
    /*
    System.out.println("new matrix");
    pp(mat);
    */
    return mat;
  }
  
  // TODO: Java 8 support! Lambdas!
  //
  // NOTE: Should use tanh instead of 1/(1+e^(-u)) as suggested by
  // http://www.willamette.edu/~gorr/classes/cs449/precond.html
  private static double sigmoid(double x) {
    return 1/(1 + Math.exp(-x));
    //return Math.tanh(x);
  }
  // derivative of sigmoid
  private static double d_sigmoid(double x) {
    return sigmoid(x) * (1 - sigmoid(x));
    // dtanh(x)/dx = sech(x)^2
    //double sech = 1.0 / Math.cosh(x);
    //return sech * sech;
  }
  
  private static double[] sigmoid(double x[]) {
    int M = x.length;
    double y[] = new double[M];
    for (int i = 0; i < M; i++) {
      y[i] = sigmoid(x[i]);
    }
    // leave last element unchanged
    y[M-1] = x[M-1];
    return y;
  }
  
  // TODO: allow normalizing input, xi' = (xi - offset) * scalar
  // TODO: allow normalizing output/feedback
  
  // convenience methods to append (wrap) and remove (unwrap)
  // a trailing -1
  public static double[] wrap(double vector[]) {
    double ret[] = Arrays.copyOf(vector, vector.length + 1);
    ret[ret.length-1] = 1;
    return ret;
  }
  public static double[] unwrap(double vector[]) {
    return Arrays.copyOf(vector, vector.length - 1);
  }
  
  public double[] process(double[] inputs) {
    // append -1, e.g. [ x0 x1 x2 -1 ]
    inputs = wrap(inputs);
    
    double layers[][] = propagate(inputs);
    double output[] = layers[layers.length - 1];
    
    // remove extra -1, [ y0 y1 -1 ]
    return unwrap(sigmoid(output));
  }
  
  // propagate a series of values through the network
  // RETURN intermediate layers, pre-sigmoid
  // assumes that inputs have been wrapped (-1 appended)
  public double[][] propagate(double[] inputs) {
    // propagate across each layer, saving pre-sigmoid output
    // length+1 to include input layer
    double outputs[][] = new double[N+1][];
    outputs[0] = inputs;
    
    // for each layer
    for(int k = 0; k < N; k++) {
      // store pre-sigmoid values in outputs[][]
      // X * W = Y
      outputs[k+1] = weights[k].preMultiply(inputs);
      
      // new X = sigmoid(Y) except last term
      inputs = sigmoid(outputs[k+1]);
    }
    return outputs;
  }
  
  // backpropagate a correct value back through the network
  // returns the error E, calculated as Sum[(xi-ti)^2] where ti are targets
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
      delta[k] = weights[k+1].transpose().preMultiply(delta[k+1]);
      // multiply each element by d_sigmoid
      for (int j = 0; j < delta[k].length; j++) {
        delta[k][j] = delta[k][j] * d_sigmoid(outputs[k+1][j]);
      }
      // last element set to 0 to backprop bias correctly
      delta[k][delta[k].length-1] = 0;
    }
    
    // update weights
    for (int k = 0; k < N; k++) { // for each layer
      // layerSlope = delta ** outputs, represents dE/dwij(** = outer product)
      // W -= learning_rate * layerSlope
      RealMatrix layerSlope =
          new ArrayRealVector(outputs[k]).outerProduct(
          new ArrayRealVector(delta[k]));
      weights[k] = weights[k].subtract(layerSlope.scalarMultiply(LEARNING_RATE));
      /*
      System.out.println("new matrix " + k);
      pp(weights[k]);
      */
    }
    
    // HACK: just checks one element, forces crash when matrix diverges
    assert !Double.isNaN(weights[0].getEntry(0,0));
  }
}
