import java.util.Arrays;

class T3Move extends AbstractMove {
  private int _x;
  public int x() { return _x; }
  private int _y;
  public int y() { return _y; }
  
  public T3Move() {
    _x = 0;
    _y = 0;
  }
  public T3Move(int i, int j) {
    _x = i;
    _y = j;
  }
  public T3Move(T3Move m) {
    _x = m._x;
    _y = m._y;
  }
  
  public boolean equals(T3Move other) {
    return _x == other._x && _y == other._y;
  }
  
  public String toString() {
    return "[" + _x + "," + _y + "]";
  }
  
  public double[] toDoubles() {
    double vector[] = new double[9];
    Arrays.fill(vector, 0.0);
    vector[_x*3+_y] = 1.0;
    return vector;
  }

  public T3Move up()    { return new T3Move(_x, _y > 0 ? _y - 1 : _y); }
  public T3Move down()  { return new T3Move(_x, _y < 2 ? _y + 1 : _y); }
  public T3Move left()  { return new T3Move(_x > 0 ? _x - 1 : _x, _y); }
  public T3Move right() { return new T3Move(_x < 2 ? _x + 1 : _x, _y); }
}
