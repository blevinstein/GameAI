import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

// represents knowledge with a neural network
class NetLearner {
  private NeuralNet _net;
  public NeuralNet net() { return _net; }
  
  public NetLearner() {
    _net = new NeuralNet(new int[]{18, 16, 9});
  }
  public NetLearner(NeuralNet n) {
    // use given net
    _net = n;
  }
  
  // choose a move
  public Move play(State s) {
    double input[] = s.toDoubles();
    double output[] = _net.process(input);
    Move m = Move.fromDoubles(output);
    //PP.pp(output);
    return m;
    /*
    if (s.validMove(m)) {
      return m;
    } else {
      return s.randomMove();
    }
    */
  }
  
  // learn by assertions about the correct move in a given state
  public void learn(State s, Move m) {
    double input[] = s.toDoubles();
    double target[] = m.toDoubles();
    _net.backpropagate(input, target);
  }
}
