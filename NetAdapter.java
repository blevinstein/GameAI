import java.util.function.Function;
import java.awt.Graphics;

// Convenience class for dealing with neural net inputs and outputs without
// explicitly converting to and from double[] each time.
//
// Only exposes selected methods.
//
// TODO: Revisit this implementation, decide whether it's worth trying to
// move this kind of stuff into the NeuralNet class itself.

public class NetAdapter<X> {
  private Function<X, double[]> _tod;
  private Function<double[], X> _fromd;

  private NeuralNet _net;
  public NeuralNet net() { return _net; }

  public NetAdapter(Function<X, double[]> tod,
                    Function<double[], X> fromd,
                    NeuralNet net) {
    _tod = tod;
    _net = net;
    _fromd = fromd;
  }

  public X process(X input) {
    return _fromd.apply(_net.process(_tod.apply(input)));
  }

  public void backpropagate(X inputs, X targets) {
    _net.backpropagate(_tod.apply(inputs), _tod.apply(targets));
  }

  public void drawState(Graphics g, X inputs, int x, int y, int sx, int sy) {
    _net.drawState(g, _tod.apply(inputs), x, y, sx, sy);
  }
}
