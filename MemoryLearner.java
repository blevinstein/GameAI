import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

// represents knowledge with a mapping of AbstractState -> Float
// can play a game and get better over time
// obvious weakness: learns every state individually
class MemoryLearner implements Learner<T3State, T3Move> {
  private double DISCOUNT = 0.9f;
  private double LEARNING_RATE = 0.3f;
  private double EPSILON = 0.10f;

  private ConcurrentHashMap<String, Double> _map;
  public Map<String, Double> map() { return _map; }
  
  public MemoryLearner() {
    this(new ConcurrentHashMap<String, Double>());
  }
  public MemoryLearner(Map<String, Double> map) {
    _map = new ConcurrentHashMap<String,Double>(map);
  }
  
  // returns set of all states in memory
  public Set<String> states() {
    return _map.keySet();
  }
  // returns remembered value for a state, defaults to state.score(..)
  public double value(AbstractState s) {
    if (!_map.containsKey(s.toString())) {
     _map.put(s.toString(), s.score());
    }
    return _map.get(s.toString());
  }
  public double value(String str) {
    if (_map.containsKey(str)) {
      return _map.get(str);
    } else {
      throw new IllegalArgumentException();
    }
  }
  // changes the remembered value for a state
  public void setValue(AbstractState s, double v) {
    setValue(s.toString(), v);
  }
  public void setValue(String str, double v) {
    _map.put(str, v);
  }
  
  // choose a move
  public T3Move query(T3State s) {
    // with some probability, choose randomly
    if (Math.random() < EPSILON) {
      return s.randomMove();
    }
    // get the set of possible moves
    T3Move allMoves[] = s.moves();
    if (allMoves.length == 0) {
      return null;
    }
    // consider every move and the resulting state; choose the best
    T3Move bestMove = allMoves[0];
    AbstractState bestState = null;
    for (T3Move move : s.moves()) {
      AbstractState newState = s.updated(move);
      if (bestState == null ||
         (value(newState) > value(bestState))) {
        bestMove = move;
        bestState = newState;
      }
    }
    //println("chose " + bestMove[0] + "," + bestMove[1] + " value " + value(bestState));
    return bestMove;
  }
  
  private ArrayList<AbstractState> shortTermMemory = new ArrayList<AbstractState>();
  
  public T3Move play(T3State s) {
    T3Move m = query(s);
    learn(s, s.updated(m));
    // TODO: remember moves and learn in reverse order, to speed backpropagation
    return m;
  }
  
  public void moveMade(T3State s, T3Move m) {
    learn(s, s.updated(m));
  }
  
  public void feedback(double f) {
    // NOTE: ignores positive/negative outcome f
  }
  
  public void teach(T3State s, T3Move m) {
    learn(s, s.updated(m));
  }
  
  // learn from two consecutive states in a game
  private void learn(AbstractState s0, AbstractState s1) {
    // the value of a state should approach the discounted value of the next state
    // i.e., V(s0) -> D * V(s1)
    double startValue = value(s0);
    double targetValue = value(s1);
    double newValue = startValue + (targetValue * DISCOUNT - startValue) * LEARNING_RATE;
    setValue(s0, newValue);
    //println("update value " + startValue + " -> " + newValue + " -> (" + targetValue + ")");
  }
  
  // learn from a chain of states, in reverse order, to allow backwards propogation
  private void learn(AbstractState s[]) {
    for (int i = s.length-1; i > 0; i--) {
      learn(s[i-1], s[i]);
    }
  }
}
