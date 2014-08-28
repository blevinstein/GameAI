// Represents a method of acting on the world controllsed by the neural network.
//
// Usage: see Sensor.java
//
// NeuralNet net = new NeuralNet( ... );
// Actuator actuator = ...;
// net.setOutput(actuator);
//
// loop {
//   net.process( ... );
// }
//
// NOTE: can be easily constructed using lambdas
// Actuator a = (ds) -> callback(ds);

interface Actuator {
  public void send(double doubles[]);
}
