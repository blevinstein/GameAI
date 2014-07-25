import java.util.HashMap;
import java.util.Set;

// represents knowledge with a neural network
class NetLearner {
  private NeuralNet _net = new NeuralNet(new int[]{9, 9, 9}); // Nein, nein, nein!
  
  public NetLearner() {
    // initialize new net
    // TODO
  }
  public NetLearner(NeuralNet n) {
    // use given net
    _net = n;
  }
  
  // choose a move
  public Move play(State s) {
    // query the net and make a move
    // TODO
    return null;
  }
  
  /*
  public void learn(State s0, State s1) {
  }
  */
  
  // learn from a chain of states, in reverse order, to allow backwards propogation
  /*
  public void learn(State s[]) {
    // maybe? TODO
  }
  */
  
  // learn by assertions about the correct move in a given state
  public void learn(State s, Move m) {
  }
}
