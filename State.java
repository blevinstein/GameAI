import java.util.ArrayList;
import java.util.Arrays;

public class State {
  static int winners[][][] = {{{0,0},{1,1},{2,2}},
                     {{0,2},{1,1},{2,0}},
                     {{0,0},{0,1},{0,2}},
                     {{1,0},{1,1},{1,2}},
                     {{2,0},{2,1},{2,2}},
                     {{0,0},{1,0},{2,0}},
                     {{0,1},{1,1},{2,1}},
                     {{0,2},{1,2},{2,2}}};

  private Square[][] _board;
  private boolean _toMove;
  private boolean _terminal = false;
  private float _value = 0f;
  
  public boolean terminal() { return _terminal; }
  public boolean toMove() { return _toMove; }
  public float value() { return _value; }
  
  public State(Square[][] b, boolean tm) {
    _board = b;
    _toMove = tm;
    check();
  }
  
  public Square board(int i, int j) {
    return _board[i][j];
  }
  
  // determines terminal and value
  public void check() {
    // check for 3 in a row
    for (int w = 0; w < winners.length; w++) {
      boolean gameWon = true;
      for (int i = 0; i < 3; i++) {
        Square sq = board(winners[w][i][0],winners[w][i][1]);
        if (sq.isEmpty() || sq.player() == toMove())
          gameWon = false;
      }
      if (gameWon) {
        _terminal = true;
        _value = !toMove() ? 1f : -1f;
      }
    }
    
    // check for a full board
    boolean boardFull = true;
    outer: for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if(board(i,j).isEmpty()) {
          boardFull = false;
          break outer;
        }
      }
    }
    if (boardFull) {
      _terminal = true;
    }
  }
  
  public boolean equals(State other) {
    if (toMove() != other.toMove()) {
      return false;
    }
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (!board(i,j).equals(other.board(i,j)))
          return false;
      }
    }
    return true;
  }
  
  public Move[] moves() {
    ArrayList<Move> m = new ArrayList<Move>();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        Move move = new Move(i, j);
        if (validMove(move)) {
          m.add(move);
        }
      }
    }
    return m.toArray(new Move[0]);
  }
  
  public boolean validMove(Move m) {
    return board(m.x,m.y).isEmpty();
  }
  
  public State clone() {
    Square newBoard[][] = _board.clone();
    return new State(newBoard, toMove());
  }
  
  public State updated(Move m) {
    assert(validMove(m));
    Square newBoard[][] = _board.clone();
    newBoard[m.x] = _board[m.x].clone();
    newBoard[m.x][m.y] = Square.mark(toMove());
    
    return new State(newBoard, !toMove());
  }
  
  public String toString() {
    String output = "";
    for(int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        output += board(i,j);
      }
      output += "/";
    }
    output += Square.mark(toMove());
    return output;
  }
  
  public String[] toLines() {
    String lines[] = new String[0];
    return lines;
  }

  public static State init() {
    Square row[] = new Square[3]; Arrays.fill(row, Square.empty());
    Square newBoard[][] = new Square[3][]; Arrays.fill(newBoard, row);
    return new State(newBoard, Math.random() < 0.5);
  }
  
  public static State fromString(String str) {
    String lines[] = str.split("/");
    Square newBoard[][] = new Square[3][];
    for(int i = 0; i < 3; i++) {
      newBoard[i] = new Square[3];
      for(int j = 0; j < 3; j++) {
        newBoard[i][j] = Square.fromChar(lines[i].charAt(j));
      }
    }
    boolean toMove = Square.fromChar(lines[3].charAt(0)).player();
    return new State(newBoard, toMove);
  }
}

