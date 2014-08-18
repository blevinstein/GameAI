import java.util.HashMap;
import java.util.Map;

class DefaultGrader {
  private static Map<String, Grader<?>> _map = new HashMap<String, Grader<?>>();

  @SuppressWarnings("unchecked")
  public static <T> Grader<T> getDefaultGrader(String className) {
    try {
      return (Grader<T>)_map.get(className);
    } catch (NullPointerException e) {
      System.err.println("Could not retrieve default grader for " + className + "!");
      System.err.println(e.getMessage());
      return null;
    }
  }

  public static <T> void registerDefaultGrader(Grader<T> grader, Class<T> klass) {
    _map.put(klass.getName(), grader);
    System.out.println("Registered default grader for " + klass + ".");
  }
}
