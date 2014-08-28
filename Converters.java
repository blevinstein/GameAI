import java.util.HashMap;
import java.util.Map;

// Static convenience methods for Converter types.

public class Converters {
  private static Map<String, Converter<?>> _map = new HashMap<>();

  @SuppressWarnings("unchecked")
  public static <T> Converter<T> get(String className) {
    try {
      return (Converter<T>)_map.get(className);
    } catch (NullPointerException e) {
      System.err.println("Could not retrieve converter of type " + className + "!");
      System.err.println(e.getMessage());
      return null;
    }
  }

  public static <T> void register(Converter<T> converter, Class<T> klass) {
    _map.put(klass.getName(), converter);
    System.out.println("Registered converter for " + klass.getName() + ".");
  }
}
