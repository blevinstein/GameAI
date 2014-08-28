// Represents a single bit of input.
//
// Could be easily replicated with
//
// boolean b;
// Sensor s = () -> b ? 1.0 : -1.0;
//
// but this provides an abstraction around the value b and its conversion.

public class BinarySensor implements Sensor {
  private boolean _v;

  public BinarySensor() { this(false); }
  public BinarySensor(boolean v) { _v = v; }

  // update by providing a new boolean value
  public void update(boolean v) { _v = v; }

  // convert to -1 or 1
  public double[] toDoubles() {
    return new double[]{ _v ? 1.0 : -1.0 };
  }
}
