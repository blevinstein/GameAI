class Square {
  public static final int X = 1;
  public static final int O = 0;
  private boolean _empty;
  private int _player;
  
  // creates an empty square
  public Square() {
    _empty = true;
  }
  // creates a marked square
  public Square(int p) {
    _empty = false;
    _player = p;
  }
  
  // flips Xs and Os
  public Square flip() {
    if (_empty)
      return this;
    return new Square(_player > 0 ? 0 : 1);
  }
  
  public boolean isEmpty() { return _empty; }
  public int player() { assert !_empty; return _player; }
  
  public boolean equals(Square other) {
    return (_empty == other.isEmpty() && _player == other._player);
  }
  
  public String toString() {
    if (!_empty) {
      if (_player > 0) {
        return "X";
      } else {
        return "O";
      }
    }
    return " ";
  }
  
  public static Square fromChar(char c) {
    switch(c) {
      case 'X': return new Square(X);
      case 'O': return new Square(O);
      case ' ': return new Square();
    }
    return null;
  }
}
