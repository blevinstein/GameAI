import java.util.Arrays;

class Move {
  public int x;
  public int y;
  
  public Move() {
    x = 0;
    y = 0;
  }
  public Move(int i, int j) {
    x = i;
    y = j;
  }
  
  public boolean equals(Move other) {
    return x == other.x && y == other.y;
  }
  
  public String toString() {
    return "[" + x + "," + y + "]";
  }
  
  public double[] toDoubles() {
    double vector[] = new double[9];
    Arrays.fill(vector, 0.0);
    vector[x*3+y] = 1.0;
    return vector;
  }
}
