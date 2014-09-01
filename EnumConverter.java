import java.util.Arrays;

// Represents a conversion between a neural network and a finite selection.
//
// E.g. ImageClassifier can match letters using an EnumConverter(26), which is
// represented by an int 0-25, or as 26 neuron inputs.

public class EnumConverter implements Converter<Integer> {
  int _n;

  public EnumConverter(int n) {
    _n = n;
  }

  public double[] toDoubles(Integer value) {
    // Accepts a value in [0, n)
    if (value < 0 || value >= _n)
      throw new IllegalArgumentException(
          "Value " + value + " is not between 0 and " + _n + "!");

    // Gives n bits of output
    double inputs[] = new double[_n];

    // All zeroes except for the chosen value
    Arrays.fill(inputs, 0.0);
    inputs[value] = 1.0;

    return inputs;
  }

  public Integer fromDoubles(double doubles[]) {
    // Accepts a set of _n inputs
    if (doubles.length != _n)
      throw new IllegalArgumentException(
          "Received " + doubles.length + "bits, expected " + _n + "!");

    // Returns the index of the largest input
    Integer result = 0;
    for (int i = 1; i < _n; i++) {
      if (doubles[i] > doubles[result])
        result = i;
    }
    return result;
  }

  public int bits() { return _n; }
}
