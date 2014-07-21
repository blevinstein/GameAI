public float DISCOUNT = 0.9;
public float ALPHA = 0.3;
public float EPSILON = 0.3;

class Learner {
  public HashMap<String, Float> value = new HashMap<String, Float>();
  public float valueOf(State s) {
    if (!value.containsKey(s.toString())) {
      if (s.won && s.winner()) {
        value.put(s.toString(), 1.0);
      } else if(s.won) {
        value.put(s.toString(), -1.0);
      } else {
        value.put(s.toString(), 0.0);
      }
    }
    return value.get(s.toString());
  }
  public int[] play(State s) {
    int allMoves[][] = s.moves();
    if (allMoves.length == 0) {
      return null;
    }
    // with some probability, choose randomly
    if (random(1.0) < EPSILON) {
      //println("random chosen");
      return allMoves[(int)random(allMoves.length)];
    }
    int bestMove[] = allMoves[0];
    State bestState = null;
    for (int move[] : s.moves()) {
      State newState = s.updated(move[0], move[1]);
      if (bestState == null ||
         (state.toMove ^ (valueOf(newState) < valueOf(bestState)))) {
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
    float newValue = startValue + (targetValue * DISCOUNT - startValue) * ALPHA;
    value.put(s0.toString(), newValue);
    //println("update value " + startValue + " -> " + newValue + " -> (" + targetValue + ")");
  }
  
  public void saveJSON(String fname) {
    println("Saving...");
    JSONArray jsonArray = new JSONArray();
    for (Entry<String, Float> entry : value.entrySet()) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.setString("state", entry.getKey());
      jsonObject.setFloat("value", entry.getValue());
      jsonArray.append(jsonObject);
    }
    saveJSONArray(jsonArray, fname);
    println("Saved " + value.size() + " knowledge to " + fname + "!");
  }
  
  public void loadJSON(String fname) {
    JSONArray jsonArray = loadJSONArray(fname);
    for (int i = 0; i < jsonArray.size(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      String keyStr = jsonObject.getString("state");
      float val = jsonObject.getFloat("value");
      value.put(keyStr, val);
    }
    println("Loaded " + jsonArray.size() + " knowledge from " + fname + "!");
  }
}
