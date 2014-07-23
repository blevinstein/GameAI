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
  
  public String toString() {
    return "[" + x + "," + y + "]";
  }
}
