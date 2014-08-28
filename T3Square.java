// Represents the state of a square in Tic Tac Toe.

class T3Square {
  public static final int X = 1;
  public static final int O = 0;
  private boolean _empty;
  private int _player;
  
  // creates an empty square
  public T3Square() {
    _empty = true;
  }
  // creates a marked square
  public T3Square(int p) {
    if (p == T3Square.O || p == T3Square.X) {
      _empty = false;
      _player = p;
    } else {
      throw new IllegalArgumentException("Cannot create a square of value " + p + ".");
    }
  }
  
  // flips Xs and Os
  public T3Square flip() {
    if (_empty)
      return this;
    return new T3Square(1 - _player);
  }
  
  public boolean isEmpty() { return _empty; }
  public int player() { return _player; }
  
  public boolean equals(T3Square other) {
    return (_empty == other.isEmpty() && _player == other._player);
  }
  
  public String toString() {
    if (!_empty) {
      if (_player == T3Square.X) {
        return "X";
      } else {
        return "O";
      }
    }
    return " ";
  }
  
  public static T3Square fromChar(char c) {
    switch(c) {
      case 'X': return new T3Square(X);
      case 'O': return new T3Square(O);
      case ' ': return new T3Square();
    }
    return null;
  }
}
