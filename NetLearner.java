import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

// represents knowledge with a neural network
class NetLearner extends AbstractLearner {
  private NeuralNet _net;
  public NeuralNet net() { return _net; }
  
  private ArrayList<double[][]> results = new ArrayList<double[][]>();
  
  public NetLearner() {
    _net = new NeuralNet(new int[]{18, 16, 9});
  }
  public NetLearner(NeuralNet n) {
    // use given net
    _net = n;
  }
  
  // choose a move
  public Move query(AbstractState s) {
    double input[] = s.toDoubles();
    double output[] = _net.process(input);
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (!s.validMove(new Move(i,j))) {
          output[i*3 + j] = 0;
        }
      }
    }
    int idx = Util.choose(output);
    Move m = new Move(idx/3, idx%3);
    assert s.validMove(m);
    return m;
  }
  
  // query and save input => output results
  public Move play(AbstractState s) {
    double input[] = s.toDoubles();
    double output[] = _net.process(input);
    
    // save input => output
    double result[][] = new double[2][];
    result[0] = input;
    result[1] = output;
    results.add(result);
    
    int idx = Util.choose(output);
    return new Move(idx/3, idx%3);
  }
  
  // remembers moves by other player, for training purposes
  public void moveMade(AbstractState s, Move m) {
    // TODO: implement
  }
  
  // learn by explicit assertions about the correct move in a given state
  public void teach(AbstractState s, Move m) {
    double input[] = s.toDoubles();
    double target[] = m.toDoubles();
    _net.backpropagate(input, target);
  }
  
  // gives positive or negative feedback to the network
  public void feedback(double f) {
    // TODO: implement
  }
  
  // forget remembered input => output results
  public void forget() {
    // TODO: implement
  }
}
