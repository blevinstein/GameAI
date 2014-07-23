import java.util.Set;

public void saveLearner(Learner l, String fname) {
  println("Saving...");
  JSONObject jsonObject = new JSONObject();
  for (String state : l.states()) {
    jsonObject.setFloat(state, l.value(state));
  }
  saveJSONObject(jsonObject, fname);
  println("Saved " + l.states().size() + " knowledge to " + fname + "!");
}

public Learner loadLearner(String fname) {
  Learner learner = new Learner();
  JSONObject jsonObject = loadJSONObject(fname);
  Set<String> keys = jsonObject.keys();
  for (String key : keys) {
    learner.setValue(key, jsonObject.getFloat(key));
  }
  println("Loaded " + jsonObject.size() + " knowledge from " + fname + "!");
  return learner;
}
