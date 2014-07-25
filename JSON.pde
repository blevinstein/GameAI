import java.util.Set;

// utility methods for saving and loading brains into JSON
// written in Processing instead of proper Java because
// Processing has a simpler JSON interface

public void saveLearner(Learner l, String fname) {
  println("Saving...");
  JSONObject jsonObject = new JSONObject();
  jsonObject.setString("player", new Square(l.player()).toString());
  for (String state : l.states()) {
    jsonObject.setFloat(state, l.value(state));
  }
  saveJSONObject(jsonObject, fname);
  println("Saved " + l.states().size() + " knowledge to " + fname + "!");
}

public Learner loadLearner(String fname) {
  JSONObject jsonObject = loadJSONObject(fname);
  String player = jsonObject.getString("player");
  Learner learner = new Learner(Square.fromChar(player.charAt(0)).player());
  Set<String> keys = jsonObject.keys();
  for (String key : keys) {
    if (key.equals("player")) continue;
    learner.setValue(key, jsonObject.getFloat(key));
  }
  println("Loaded " + learner.states().size() + " knowledge from " + fname + "!");
  return learner;
}
