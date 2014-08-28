// Represents a source of information for a neural network.
//
// TODO: add normalization capabilities?
// TODO: add a length() function, to allow checking for dimension mismatch on
//   construction? this would disallow any Sensor implementation that didn't
//   know its bit width in advance
//
// Usage:
//
// NeuralNet net = new NeuralNet( ... );
// Sensor sensor = ...;
// Actuator actuator = ...;
// net.setInput(sensor);
// net.setOutput(actuator);
//
// loop {
//   input.updateSomehow();
//   // will call actuator.send(double[])
// }
//
// NOTE: can be easily constructed using lambdas
// Sensor s = () -> getDoubles();

interface Sensor {
  public double[] toDoubles();

  default int length() { return toDoubles().length; }
}
