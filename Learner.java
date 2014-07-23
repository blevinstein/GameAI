import java.util.HashMap;
import java.util.Set;

class Learner {
  private float DISCOUNT = 0.9f;
  private float LEARNING_RATE = 0.3f;
  private float EPSILON = 0.02f;
  private HashMap<String, Float> _value;
  public Learner() {
    _value = new HashMap<String, Float>();
  }
  public Learner(HashMap<String, Float> map) {
    _value = map;
    /*
    TODO: make this work
    DISCOUNT = params.getFloat("discount");
    LEARNING_RATE = params.getFloat("learning_rate");
    EPSILON = params.getFloat("epsilon");
    */
  }
  public Set<String> states() {
    return _value.keySet();
  }
  public float value(State s) {
    if (!_value.containsKey(s.toString())) {
     _value.put(s.toString(), s.value());
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
  public void setValue(State s, float v) {
    setValue(s.toString(), v);
  }
  public void setValue(String str, float v) {
    _value.put(str, v);
  }
  public Move play(State s) {
    Move allMoves[] = s.moves();
    if (allMoves.length == 0) {
      return null;
    }
    // with some probability, choose randomly
    if (Math.random() < EPSILON) {
      //println("random chosen");
      return allMoves[(int)(Math.random() * allMoves.length)];
    }
    Move bestMove = allMoves[0];
    State bestState = null;
    for (Move move : s.moves()) {
      State newState = s.updated(move);
      if (bestState == null ||
         (s.toMove() ^ (value(newState) < value(bestState)))) {
        bestMove = move;
        bestState = newState;
      }
    }
    //println("chose " + bestMove[0] + "," + bestMove[1] + " value " + value(bestState));
    return bestMove;
  }
  
  public void learn(State s0, State s1) {
    float startValue = value(s0);
    float targetValue = value(s1);
    float newValue = startValue + (targetValue * DISCOUNT - startValue) * LEARNING_RATE;
    setValue(s0, newValue);
    //println("update value " + startValue + " -> " + newValue + " -> (" + targetValue + ")");
  }
  
  public void learn(State s[]) {
    for (int i = s.length-1; i > 0; i--) {
      learn(s[i-1], s[i]);
    }
  }
}
