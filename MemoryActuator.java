import java.util.function.Function;

// Handles a piece of output by converting and storing a value.
//
// Retrieve the value with get().
//
// NOTE: can be easily constructed using lambdas
// MemoryActuator<boolean> = new MemoryActuator<>(xs -> xs[0] > 0);

public class MemoryActuator<T> implements Actuator {
  // for converting raw data into a value
  Function <double[], T> _f;

  // for storing the value
  T _mem;
  public T get() { return _mem; }

  public MemoryActuator(Function<double[], T> f) { _f = f; }

  public void send(double doubles[]) {
    _mem = _f.apply(doubles);
  }
}
