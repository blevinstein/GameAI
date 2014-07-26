import java.util.HashMap;
import java.util.Set;

// represents knowledge with a mapping of AbstractState -> Float
// can play a game and get better over time
// obvious weakness: learns every state individually
class MemoryLearner {
  private float DISCOUNT = 0.9f;
  private float LEARNING_RATE = 0.3f;
  private float EPSILON = 0.10f;
  private HashMap<String, Float> _value;
  private int _player;
  
  public int player() { return _player; } // used when calling state.score(player)
  
  public MemoryLearner(int player) {
    this(new HashMap<String, Float>(), player);
  }
  public MemoryLearner(HashMap<String, Float> map, int player) {
    _value = map;
    _player = player;
  }
  
  // returns set of all states in memory
  public Set<String> states() {
    return _value.keySet();
  }
  // returns remembered value for a state, defaults to state.score(..)
  public float value(AbstractState s) {
    if (!_value.containsKey(s.toString())) {
     _value.put(s.toString(), s.score(_player));
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
  public Move play(AbstractState s) {
    assert s.toMove() == _player;
    // get the set of possible moves
    Move allMoves[] = s.moves();
    if (allMoves.length == 0) {
      return null;
    }
    // with some probability, choose randomly
    if (Math.random() < EPSILON) {
      //println("random chosen");
      return allMoves[(int)(Math.random() * allMoves.length)];
    }
    // consider every move and the resulting state; choose the best
    Move bestMove = allMoves[0];
    AbstractState bestState = null;
    for (Move move : s.moves()) {
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
  
  // learn from two consecutive states in a game
  public void learn(AbstractState s0, AbstractState s1) {
    // the value of a state should approach the discounted value of the next state
    // i.e., V(s0) -> D * V(s1)
    float startValue = value(s0);
    float targetValue = value(s1);
    float newValue = startValue + (targetValue * DISCOUNT - startValue) * LEARNING_RATE;
    setValue(s0, newValue);
    //println("update value " + startValue + " -> " + newValue + " -> (" + targetValue + ")");
  }
  
  // learn from a chain of states, in reverse order, to allow backwards propogation
  public void learn(AbstractState s[]) {
    for (int i = s.length-1; i > 0; i--) {
      learn(s[i-1], s[i]);
    }
  }
}
