package com.blevinstein.net;

import static com.blevinstein.net.NeuralNet2.Style;

import java.util.function.Function;
import java.awt.Graphics;

// Convenience class for dealing with neural net inputs and outputs without
// explicitly converting to and from double[] each time.
//
// Only exposes selected methods.
//
// TODO: Revisit this implementation, decide whether it's worth trying to
// move this kind of stuff into the NeuralNet class itself.

public class NetAdapter<X, Y> {
  private Converter<X> cin;
  private Converter<Y> cout;

  private NeuralNet2 net;
  public NeuralNet2 getNet() { return net; }
  public void setNet(NeuralNet2 net) { this.net = net; }

  public NetAdapter(Converter<X> cin, Converter<Y> cout) {
    this.cin = cin;
    this.cout = cout;
    this.net = new NeuralNet2(cin.bits(), cout.bits());
  }

  public Y process(X input) {
    Signal inputSignal = cin.toSignal(input);
    Signal outputSignal = net.apply(inputSignal);
    return cout.fromSignal(outputSignal);
  }
  
  public void backpropagate(X input, Y target) {
    Signal inputSignal = cin.toSignal(input);
    Signal targetSignal = cout.toSignal(target);
    net.backpropagate(inputSignal, targetSignal);
  }

  public void drawState(Graphics g, X input, int x, int y, int sx, int sy) {
    net.drawState(g, cin.toSignal(input), x, y, sx, sy);
  }
  public void drawState(Graphics g, X input, int x, int y, int sx, int sy, Style mode) {
    net.drawState(g, cin.toSignal(input), x, y, sx, sy, mode);
  }
}
