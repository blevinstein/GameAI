import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

// TODO: add console output when saving/loading

class Json {
  static Gson gson = new Gson();
  public static void saveNet(NeuralNet net, String fname) {
    RealMatrix weights[] = net.getWeights();
    double matrices[][][] = new double[weights.length][][];
    for (int k = 0; k < matrices.length; k++) {
      double matrix[][] = new double[weights[k].getRowDimension()][];
      for (int i = 0; i < matrix.length; i++) {
        double row[] = new double[weights[k].getColumnDimension()];
        for (int j = 0; j < row.length; j++) {
          row[j] = weights[k].getEntry(i,j);
        }
        matrix[i] = row;
      }
      matrices[k] = matrix;
    }
    try {
      FileUtils.writeStringToFile(new File(fname), gson.toJson(matrices));
    } catch(IOException e) {
      System.err.println("Could not save net!");
    }
  }
  
  public static NeuralNet loadNet(String fname) {
    ArrayList<RealMatrix> weights = new ArrayList<RealMatrix>();
  
    try {
      double matrices[][][] = gson.fromJson(FileUtils.readFileToString(new File(fname)), double[][][].class);
      for (int k = 0; k < matrices.length; k++) {
        weights.add(new Array2DRowRealMatrix(matrices[k]));
      }
      return new NeuralNet(weights.toArray(new RealMatrix[0]));
    } catch (IOException e) {
      System.err.println("Could not load net!");
      return null;
    }
  }
  
  public static void saveMap(HashMap map, String fname) {
    try {
      FileUtils.writeStringToFile(new File(fname), gson.toJson(map));
    } catch (IOException e) {
      System.err.println("Could not save map!");
    }
  }
  
  public static HashMap loadMap(String fname) {
    try {
      HashMap<String, Float> hm = new HashMap<String, Float>();
      hm = (HashMap<String, Float>)gson.fromJson(FileUtils.readFileToString(new File(fname)), hm.getClass());
      return hm;
    } catch (IOException e) {
      System.err.println("Could not load map! " + e);
      return null;
    }
  }
}
