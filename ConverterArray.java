import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConverterArray implements Converter<List<?>> {
  List<Converter<?>> _converters;

  public ConverterArray(List<Converter<?>> converters) {
    _converters = converters;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public double[] toDoubles(List<?> values) {
    // TODO: do this with streams instead, implement ZipStream(List, List)?

    double allResults[] = new double[bits()];
    int i = 0;
    for (int v = 0; v < values.size(); v++) { // for each value/converter pair
      // get doubles
      Converter c = _converters.get(v);
      double results[] = c.toDoubles(values.get(v));

      // check output length
      assert results.length == c.bits();

      // copy results into larger array
      System.arraycopy(results, 0, allResults, i, results.length);
      i += results.length;
    }
    return allResults;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public List<?> fromDoubles(double[] doubles) {
    // check input length
    assert doubles.length == bits();

    List results = new ArrayList<>();

    int i = 0;
    for (int v = 0; v < _converters.size(); v++) { // for each converter
      // get value from doubles, add to list
      Converter c = _converters.get(v);
      int bits = c.bits();
      results.add(c.fromDoubles(Arrays.copyOfRange(doubles, i, bits)));
      i += bits;
    }
    return results;
  }

  public int bits() {
    return _converters.stream().mapToInt(c -> c.bits()).sum();
  }
}
