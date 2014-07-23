class Square {
  public static final boolean X = true;
  public static final boolean O = false;
  private boolean full;
  private boolean player;
  public Square(boolean f, boolean p) {
    full = f;
    player = p;
  }
  
  public boolean equals(Square other) {
    return (full == other.full && player == other.player);
  }
  
  public String toString() {
    if (full)
      if (player)
        return "X";
      else
        return "O";
    return " ";
  }
  
  public boolean isEmpty() {
    return !full;
  }
  
  public boolean player() {
    assert(!isEmpty());
    return player;
  }
  
  public static Square fromChar(char c) {
    switch(c) {
      case 'X': return mark(true);
      case 'O': return mark(false);
      case ' ': return empty();
    }
    return null;
  }
  
  public static Square empty() {
    return new Square(false, false);
  }
  public static Square mark(boolean p) {
    return new Square(true, p);
  }
}
