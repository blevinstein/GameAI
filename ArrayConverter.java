import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

public class ArrayConverter<T> implements Converter<T[]> {
  Converter<T> _converter;
  int _length;
  Class<T> _klass;

  // klass argument required to work around weaknesses in Java generics
  public ArrayConverter(Class<T> klass, Converter<T> converter, int length) {
    if (converter == null) throw new NullPointerException();
    _klass = klass;
    _converter = converter;
    _length = length;
  }

  public double[] toDoubles(T values[]) {
    if (values.length != _length)
      throw new RuntimeException("Wrong input size!");
    int bits = _converter.bits();
    double result[] = new double[bits()];
    for (int i = 0; i < values.length; i++) {
      double partial[] = _converter.toDoubles(values[i]);
      if (partial.length != bits)
        throw new RuntimeException("Wrong partial result length!");
      System.arraycopy(partial, 0, result, bits * i, bits);
    }
    return result;
    /*
    return Arrays.stream(values)
      .flatMapToDouble(v -> Arrays.stream(_converter.toDoubles(v)))
      .toArray();
    */
  }

  @SuppressWarnings("unchecked")
  public T[] fromDoubles(double doubles[]) {
    int bits = _converter.bits();
    T result[] = (T[]) Array.newInstance(_klass, _length);
    for (int i = 0; i < _length; i++) {
      result[i] = _converter.fromDoubles(Arrays.copyOfRange(doubles, i * bits, bits));
    }
    return result;
    /*
    int bits = _converter.bits();
    return (T[])Stream
      .iterate(0, i -> i + bits).limit(_length) // i = 0 step by bits
      .map(i -> _converter.fromDoubles(Arrays.copyOfRange(doubles, i, bits)))
      .toArray();
    */
  }

  public int bits() {
    return _converter.bits() * _length;
  }
}
