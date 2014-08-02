import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

// represents knowledge with a mapping of AbstractState -> Float
// can play a game and get better over time
// obvious weakness: learns every state individually
class MemoryLearner extends AbstractLearner<T3State, T3Move> {
  private float DISCOUNT = 0.9f;
  private float LEARNING_RATE = 0.3f;
  private float EPSILON = 0.10f;
  
  private HashMap<String, Float> _value;
  
  public MemoryLearner() {
    this(new HashMap<String, Float>());
  }
  public MemoryLearner(HashMap<String, Float> map) {
    _value = map;
  }
  
  // returns set of all states in memory
  public Set<String> states() {
    return _value.keySet();
  }
  // returns remembered value for a state, defaults to state.score(..)
  public float value(AbstractState s) {
    if (!_value.containsKey(s.toString())) {
     _value.put(s.toString(), s.score());
    }
    return _value.get(s.toString());
  }
  public float value(String str) {
    if (_value.containsKey(str)) {
      return _value.get(str);
    } else {
      throw new IllegalArgumentException();
    }
  }
  // changes the remembered value for a state
  public void setValue(AbstractState s, float v) {
    setValue(s.toString(), v);
  }
  public void setValue(String str, float v) {
    _value.put(str, v);
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
    float startValue = value(s0);
    float targetValue = value(s1);
    float newValue = startValue + (targetValue * DISCOUNT - startValue) * LEARNING_RATE;
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
