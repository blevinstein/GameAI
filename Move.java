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
  
  public static Move fromDoubles(double vector[]) {
    int index = 0;
    for (int i = 1; i < 9; i++) { // get index of largest output signal
      if (vector[i] > vector[index]) {
        index = i;
      }
    }
    return new Move(index/3, index%3);
  }
}
