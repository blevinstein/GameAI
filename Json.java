import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSerializationContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

class Json {
  static Gson gson = init();

  public static Gson init() {
    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
    builder.registerTypeAdapter(Population.class, new PopulationSerializer());
    builder.registerTypeAdapter(Population.class, new PopulationDeserializer());
    /*
    builder.registerTypeAdapter(NeuralNet.class, new NeuralNetSerializer());
    builder.registerTypeAdapter(NeuralNet.class, new NeuralNetDeserializer());
    */
    return builder.create();
  }

  private static class PopulationSerializer
      implements JsonSerializer<Population<NeuralNet>> {
    public JsonElement serialize(Population<NeuralNet> src,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {
      JsonArray array = new JsonArray();
      for (NeuralNet net : src.pop()) {
        array.add(gson.toJsonTree(net.toDoubles()));
      }
      return array;
    }
  }

  @SuppressWarnings("unchecked")
  private static class PopulationDeserializer
      implements JsonDeserializer<Population<?>> {
    public Population<NeuralNet> deserialize(JsonElement json,
                                             Type type,
                                             JsonDeserializationContext context)
                                             throws JsonParseException {
      double[][][][] array = gson.fromJson(json, double[][][][].class);
      List<NeuralNet> list = new ArrayList<NeuralNet>();
      for (double[][][] subarray : array) list.add(new NeuralNet(subarray));
      return new Population<NeuralNet>(list);
    }
  }

  // TODO: make Population[Des,S]erializer work for Population<?>
  /*
  public static class NeuralNetSerializer
      implements JsonSerializer<NeuralNet> {
    public JsonElement serialize(NeuralNet src,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {
      System.out.println("NeuralNet Serializer");
      // serialize as double[][][]
      return gson.toJsonTree(src.toDoubles());
    }
  }
  
  public static class NeuralNetDeserializer
      implements JsonDeserializer<NeuralNet> {
    public NeuralNet deserialize(JsonElement json,
                                  Type type,
                                  JsonDeserializationContext context)
                                  throws JsonParseException {
      System.out.println("NeuralNet Deserializer");
      // deserialize from double[][][]
      return new NeuralNet(gson.fromJson(json, double[][][].class));
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
      return null;
    }
  }
}
