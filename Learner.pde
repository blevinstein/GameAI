import java.util.Set;
import java.util.Map.Entry;

class Learner {
  private float DISCOUNT = 0.9;
  private float LEARNING_RATE = 0.3;
  private float EPSILON = 0.02;
  private HashMap<String, Float> value = new HashMap<String, Float>();
  public Learner() {
    /*
    TODO: make this work
    DISCOUNT = params.getFloat("discount");
    LEARNING_RATE = params.getFloat("learning_rate");
    EPSILON = params.getFloat("epsilon");
    */
  }
  public float valueOf(State s) {
    if (!value.containsKey(s.toString())) {
     value.put(s.toString(), s.value());
    }
    return value.get(s.toString());
  }
  public Move play(State s) {
    Move allMoves[] = s.moves();
    if (allMoves.length == 0) {
      return null;
    }
    // with some probability, choose randomly
    if (random(1.0) < EPSILON) {
      //println("random chosen");
      return allMoves[(int)random(allMoves.length)];
    }
    Move bestMove = allMoves[0];
    State bestState = null;
    for (Move move : s.moves()) {
      State newState = s.updated(move);
      if (bestState == null ||
         (state.toMove() ^ (valueOf(newState) < valueOf(bestState)))) {
        bestMove = move;
        bestState = newState;
      }
    }
    //println("chose " + bestMove[0] + "," + bestMove[1] + " value " + valueOf(bestState));
    return bestMove;
  }
  
  public void learn(State s0, State s1) {
    float startValue = valueOf(s0);
    float targetValue = valueOf(s1);
    float newValue = startValue + (targetValue * DISCOUNT - startValue) * LEARNING_RATE;
    value.put(s0.toString(), newValue);
    //println("update value " + startValue + " -> " + newValue + " -> (" + targetValue + ")");
  }
  
  public void learn(State s[]) {
    for (int i = s.length-1; i > 0; i--) {
      learn(s[i-1], s[i]);
    }
  }
  
  public void saveJSON(String fname) {
    println("Saving...");
    JSONObject jsonObject = new JSONObject();
    for (Entry<String, Float> entry : value.entrySet()) {
      jsonObject.setFloat(entry.getKey(), entry.getValue());
    }
    saveJSONObject(jsonObject, fname);
    println("Saved " + value.size() + " knowledge to " + fname + "!");
  }
  
  public void loadJSON(String fname) {
    JSONObject jsonObject = loadJSONObject(fname);
    Set<String> keys = jsonObject.keys();
    for (String key : keys) {
      value.put(key, jsonObject.getFloat(key));
    }
    println("Loaded " + value.size() + " knowledge from " + fname + "!");
  }
}
