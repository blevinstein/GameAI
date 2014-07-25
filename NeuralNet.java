import java.util.Arrays;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

// represents a neural network using matrices
public class NeuralNet {
  //[ y0 ]   [ w00 w01 w02 t0 ][ x0 ]
  //[ y1 ] = [ w10 w11 w12 t1 ][ x1 ]
  //[ -1 ]   [ 0   0   0   1  ][ x2 ]
  //                           [ -1 ]
  // yi = Sum(wij * xj - ti)
  // t = threshold
  private RealMatrix weights[];
  public NeuralNet(int neurons[]) {
    weights = new RealMatrix[neurons.length - 1];
    for (int i = 0; i < weights.length; i++) {
      weights[i] = newMatrix(neurons[i+1], neurons[i]);
    }
  }
  // creates an M x N
  public RealMatrix newMatrix(int inputs, int outputs) {
    RealMatrix mat = new Array2DRowRealMatrix(outputs+1, inputs+1);
    // initialize matrix values between -1 and 1 times max_weight
    for (int i = 0; i < outputs; i++) {
      for (int j = 0; j < inputs + 1; j++) {
        // NOTE: max_weight = 1 / sqrt(Ai) where Ai = fan-in to node i
        // http://www.willamette.edu/~gorr/classes/cs449/precond.html
        mat.setEntry(i, j, (Math.random() * 2 - 1) * (1 / Math.sqrt(inputs)));
      }
    }
    // last row is [ 0 0 ..  1 ]
    for (int j = 0; j < inputs; j++) {
      mat.setEntry(outputs, j, 0);
    }
    mat.setEntry(outputs, inputs, 0);
    return mat;
  }
  //TODO: Java 8 support! Lambdas!
  //
  // NOTE: Using tanh instead of 1/(1+e^(-u)) by suggestion
  // http://www.willamette.edu/~gorr/classes/cs449/precond.html
  public static double sigmoid(double x) {
    return Math.tanh(x);
  }
  
  // TODO: allow normalizing input, xi' = (xi - offset) * scalar
  // TODO: allow normalizing output/feedback
  
  public double[] process(double[] inputs) {
    double layers[][] = propagate(inputs);
    double output[] = layers[layers.length - 1];
    // remove extra -1, [ y0 y1 -1 ]
    return Arrays.copyOf(output, output.length - 1);
  }
  
  // propagate a series of values through the network
  // RETURN intermediate layers, pre-sigmoid
  public double[][] propagate(double[] inputs) {
    // append -1, e.g. [ x0 x1 x2 -1 ]
    inputs = Arrays.copyOf(inputs, inputs.length + 1); inputs[inputs.length-1] = 1;
    // propagate across each layer, saving pre-sigmoid output
    double outputs[][] = new double[weights.length][];
    // for each matrix between layers
    for(int i = 0; i < weights.length; i++) {
      outputs[i] = weights[i].preMultiply(inputs);
      inputs = outputs[i].clone();
      // new_inputs = sigmoid(Y)
      // NOTE length-1 to exclude bias
      for(int k = 0; k < outputs.length-1; k++) {
        inputs[k] = sigmoid(inputs[k]);
      }
    }
    return outputs;
  }
  
  public void backpropagate(double[] inputs, double[] targets) {
    double outputs[][] = propagate(inputs);
    // TODO: implement
  }
}
