import java.util.Set;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

// utility methods for saving and loading brains into JSON
// written in Processing instead of proper Java because
// Processing has a simpler JSON interface

public void saveNet(NeuralNet net, String fname) {
  RealMatrix weights[] = net.getWeights();
  JSONArray matrices = new JSONArray();
  for (int k = 0; k < weights.length; k++) {
    JSONArray matrix = new JSONArray();
    for (int i = 0; i < weights[k].getRowDimension(); i++) {
      JSONArray row = new JSONArray();
      for (int j = 0; j < weights[k].getColumnDimension(); j++) {
        row.setFloat(j, (float)weights[k].getEntry(i,j));
      }
      matrix.setJSONArray(i, row);
    }
    matrices.setJSONArray(k, matrix);
  }
  saveJSONArray(matrices, fname);
  println("Saved net of " + PP.dimOf(weights));
}

public NeuralNet loadNet(String fname) {
  ArrayList<RealMatrix> weights = new ArrayList<RealMatrix>();
  
  JSONArray jsonMatrices = loadJSONArray(fname);
  for (int k = 0; k < jsonMatrices.size(); k++) {
    JSONArray jsonMatrix = jsonMatrices.getJSONArray(k);
    double doubles[][] = new double[jsonMatrix.size()][];
    for (int i = 0; i < jsonMatrix.size(); i++) {
      JSONArray jsonRow = jsonMatrix.getJSONArray(i);
      doubles[i] = new double[jsonRow.size()];
      for (int j = 0; j < jsonRow.size(); j++) {
        doubles[i][j] = jsonRow.getFloat(j);
      }
    }
    weights.add(new Array2DRowRealMatrix(doubles));
  }
  println("Loaded net of " + PP.dimOf(weights.toArray(new RealMatrix[0])));
  return new NeuralNet(weights.toArray(new RealMatrix[0]));
}

public void saveLearner(MemoryLearner l, String fname) {
  JSONObject jsonObject = new JSONObject();
  for (String state : l.states()) {
    jsonObject.setFloat(state, l.value(state));
  }
  saveJSONObject(jsonObject, fname);
  println("Saved " + l.states().size() + " knowledge to " + fname + "!");
}

public MemoryLearner loadLearner(String fname) {
  JSONObject jsonObject = loadJSONObject(fname);
  HashMap<String, Float> values = new HashMap<String, Float>();
  Set<String> keys = jsonObject.keys();
  for (String key : keys) {
    values.put(key, jsonObject.getFloat(key));
  }
  MemoryLearner learner = new MemoryLearner(values);
  println("Loaded " + learner.states().size() + " knowledge from " + fname + "!");
  return learner;
}
