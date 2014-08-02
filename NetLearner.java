import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

// represents knowledge with a neural network
class NetLearner extends AbstractLearner<T3State, T3Move> {
  private double EPSILON = 0.1;
  
  private NeuralNet _net;
  public NeuralNet net() { return _net; }
  
  private ArrayList<double[][]> myMoves = new ArrayList<double[][]>();
  private ArrayList<double[][]> otherMoves = new ArrayList<double[][]>();
  
  public NetLearner() {
    _net = new NeuralNet(new int[]{18, 16, 9});
  }
  public NetLearner(NeuralNet n) {
    // use given net
    _net = n;
  }
  
  // choose a move
  public T3Move query(T3State s) {
    // with some probability, choose randomly
    if (Math.random() < EPSILON) {
      return s.randomMove();
    }
    
    double input[] = s.toDoubles();
    double output[] = _net.process(input);
    // disable invalid moves
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (!s.validMove(new T3Move(i,j))) {
          output[i*3 + j] = 0;
        }
      }
    }
    int idx = Util.choose(output);
    T3Move m = new T3Move(idx/3, idx%3);
    return m;
  }
  
  // query and save input => output results
  public T3Move play(T3State s) {
    double input[] = s.toDoubles();
    T3Move m = query(s);
    
    // save input => output
    double move[][] = new double[2][];
    move[0] = input;
    move[1] = m.toDoubles();
    myMoves.add(move);
    
    return m;
  }
  
  // remembers moves by other player, for training purposes
  public void moveMade(T3State s, T3Move m) {
    double move[][] = new double[2][];
    move[0] = s.toDoubles();
    move[1] = m.toDoubles();
    otherMoves.add(move);
  }
  
  // learn by explicit assertions about the correct move in a given state
  public void teach(T3State s, T3Move m) {
    double input[] = s.toDoubles();
    double target[] = m.toDoubles();
    _net.backpropagate(input, target);
  }
  
  public void teach(ArrayList<double[][]> moves) {
    for (int i = 0; i < moves.size(); i++) {
      double input[] = moves.get(i)[0];
      double output[] = moves.get(i)[1];
      _net.backpropagate(input, output);
    }
  }
  
  // gives positive or negative feedback to the network
  public void feedback(double f) {
    if (f > 0.0) {
      teach(myMoves);
    } else if (f < 0.0) {
      teach(otherMoves);
    }
    forget();
  }
  
  // forget remembered input => output results
  public void forget() {
    myMoves.clear();
    otherMoves.clear();
  }
}
