import java.util.function.Function;
import java.awt.Graphics;

// Convenience class for dealing with neural net inputs and outputs without
// explicitly converting to and from double[] each time.
//
// Only exposes selected methods.
//
// TODO: Revisit this implementation, decide whether it's worth trying to
// move this kind of stuff into the NeuralNet class itself.

public class NetAdapter<X,Y> {
  private Converter<X> _cin;
  private Converter<Y> _cout;

  private NeuralNet _net;
  public NeuralNet net() { return _net; }
  public void setNet(NeuralNet net) { _net = net; }

  public NetAdapter(Converter<X> cin, Converter<Y> cout, NeuralNet net) {
    _cin = cin;
    _cout = cout;
    _net = net;
  }

  public Y process(X input) {
    double[] rawInput = _cin.toDoubles(input);
    double[] rawOutput = _net.process(rawInput);
    return _cout.fromDoubles(rawOutput);
  }

  public void backpropagate(X input, Y target) {
    double[] rawInput = _cin.toDoubles(input);
    double[] rawTarget = _cout.toDoubles(target);
    _net.backpropagate(rawInput, rawTarget);
  }

  public void drawState(Graphics g, X input, int x, int y, int sx, int sy) {
    _net.drawState(g, _cin.toDoubles(input), x, y, sx, sy);
  }
}
