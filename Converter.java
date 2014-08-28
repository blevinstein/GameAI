interface Converter<T> {
  public double[] toDoubles(T value);
  public T fromDoubles(double doubles[]);
  public int bits(); // return the bit width of this converter
}
