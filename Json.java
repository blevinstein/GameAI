import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

// TODO: add console output when saving/loading

class Json {
  static Gson gson = new Gson();

  public static void savePop(Population pop, String fname) {
    double genomes[][] = pop.toDoubles();
    try {
      FileUtils.writeStringToFile(new File(fname), gson.toJson(genomes));
      System.out.println("Saved population of " + pop.pop().size() + " to " + fname + ".");
    } catch(IOException e) {
      System.err.println("Could not save pop!");
    }
  }

  public static Population loadPop(String fname, Grader grader) {
    try {
      double genomes[][] = gson.fromJson(FileUtils.readFileToString(new File(fname)), double[][].class);
      System.out.println("Loaded population of " + genomes.length + ".");
      return new Population(grader, genomes);
    } catch(IOException e) {
      System.err.println("Could not load pop!");
      return null;
    }
  }

  public static void saveNet(NeuralNet net, String fname) {
    RealMatrix weights[] = net.weights();
    double matrices[][][] = net.toDoubles();
    try {
      FileUtils.writeStringToFile(new File(fname), gson.toJson(matrices));
      System.out.println("Saved neural net of dimension " + PP.dimOf(weights) + " to " + fname + ".");
    } catch(IOException e) {
      System.err.println("Could not save net!");
    }
  }
  
  public static NeuralNet loadNet(String fname) {
    ArrayList<RealMatrix> weights = new ArrayList<RealMatrix>();
  
    try {
      double matrices[][][] = gson.fromJson(FileUtils.readFileToString(new File(fname)), double[][][].class);
      System.out.println("Loaded neural net.");
      return new NeuralNet(matrices);
    } catch (IOException e) {
      System.err.println("Could not load net!");
      return null;
    }
  }
  
  public static void saveMap(Map<String,Double> map, String fname) {
    try {
      FileUtils.writeStringToFile(new File(fname), gson.toJson(map));
      System.out.println("Saved map of " + map.size() + " knowledge to " + fname + ".");
    } catch (IOException e) {
      System.err.println("Could not save map!");
    }
  }
 
  @SuppressWarnings(value = "unchecked")
  public static Map<String, Double> loadMap(String fname) {
    try {
      Map<String, Double> hm = gson.fromJson(FileUtils.readFileToString(new File(fname)), HashMap.class);
      System.out.println("Loaded map of " + hm.size() + " knowledge.");
      return hm;
    } catch (IOException e) {
      System.err.println("Could not load map! " + e);
      return null;
    }
  }
}
