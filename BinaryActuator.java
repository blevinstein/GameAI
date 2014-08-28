public class BinaryActuator extends MemoryActuator<Boolean> {
  public BinaryActuator() {
    super(xs -> xs[0] > 0);
  }
}
