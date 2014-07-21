import java.util.Map.Entry;

class Square {
  public boolean full;
  public boolean player;
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
}

public Square fromChar(char c) {
  switch(c) {
    case 'X': return mark(true);
    case 'O': return mark(false);
    case ' ': return empty();
  }
  println("Invalid character -> square. '" + c + "'");
  return empty();
}
  
public Square empty() {
  return new Square(false, false);
}
public Square mark(boolean p) {
  return new Square(true, p);
}
