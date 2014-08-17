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

// TODO: write serializers and deserializers for each class

class Json {
  static Gson gson = new Gson();

  /*
  public static void savePop(Population<T> pop, String fname) {
    double genomes[][] = pop.toDoubles();
    try {
      FileUtils.writeStringToFile(new File(fname), gson.toJson(genomes));
      System.out.println("Saved population of " + pop.pop().size() + " to " + fname + ".");
    } catch(IOException e) {
      System.err.println("Could not save pop!");
    }
  }

  public static <T> Population<T> loadPop(String fname, Grader grader) {
    try {
      double genomes[][] = gson.fromJson(FileUtils.readFileToString(new File(fname)), double[][].class);
      System.out.println("Loaded population of " + genomes.length + ".");
      return new Population<T>(grader, genomes);
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
  */

  public static <T> void save(T object, String fname) {
    String type = object.getClass().getName();
    try {
      FileUtils.writeStringToFile(new File(fname), gson.toJson(object));
      System.out.println("Saved a " + type + " to " + fname + ".");
    } catch (IOException e) {
      System.err.println("Could not save a " + type + " to " + fname + "!");
    }
  }

  public static <T> T load(String fname, Class<T> klass) {
    String type = klass.getName();
    try {
      T object = gson.fromJson(FileUtils.readFileToString(new File(fname)), klass);
      System.out.println("Loaded a " + type + " from " + fname + ".");
      return object;
    } catch (IOException e) {
      System.err.println("Could not load a " + type + " from " + fname + "!");
    }
  }
}
