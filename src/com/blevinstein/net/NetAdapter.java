package com.blevinstein.net;

import com.blevinstein.net.Util.Style;

import com.google.common.collect.ImmutableList;
import java.awt.Graphics;

// Convenience class for dealing with neural net inputs and outputs without
// explicitly converting to and from double[] each time.
//
// Only exposes selected methods.

public class NetAdapter<X, Y> {
  private Converter<X> cin;
  private Converter<Y> cout;

  private NeuralNet net;
  public NeuralNet getNet() { return net; }
  public void setNet(NeuralNet net) { this.net = net; }

  public NetAdapter(Converter<X> cin, Converter<Y> cout) {
    this.cin = cin;
    this.cout = cout;
    this.net = NeuralNet.create(
        ImmutableList.of(cin.bits(), Math.max(cin.bits(), cout.bits()), cout.bits()));
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
    Util.drawState(g, net, cin.toSignal(input), x, y, sx, sy);
  }
  public void drawState(Graphics g, X input, int x, int y, int sx, int sy, Style mode) {
    Util.drawState(g, net, cin.toSignal(input), x, y, sx, sy, mode);
  }
}
