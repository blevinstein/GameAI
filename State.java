import java.util.ArrayList;
import java.util.Arrays;

public class State extends AbstractState {
  static int winners[][][] = {{{0,0},{1,1},{2,2}},
                     {{0,2},{1,1},{2,0}},
                     {{0,0},{0,1},{0,2}},
                     {{1,0},{1,1},{1,2}},
                     {{2,0},{2,1},{2,2}},
                     {{0,0},{1,0},{2,0}},
                     {{0,1},{1,1},{2,1}},
                     {{0,2},{1,2},{2,2}}};

  private Square[][] _board;
  private int _toMove;
  private boolean _terminal = false;
  private float _score = 0f;
  
  public boolean terminal() { return _terminal; }
  public int toMove() { return _toMove; }
  
  // NOTE: _score is positive for X, negative for O
  public float score(int player) { return player > 0 ? _score : -_score; }
  
  public State() {
    Square row[] = new Square[3]; Arrays.fill(row, new Square());
    Square newBoard[][] = new Square[3][]; Arrays.fill(newBoard, row);
    _board = newBoard;
    _toMove = Math.random() < 0.5 ? 1 : 0;
    check();
  }
  public State(Square[][] b, int tm) {
    _board = b;
    _toMove = tm;
    check();
  }
  public State(String str) {
    String lines[] = str.split("/");
    _board = new Square[3][];
    for(int i = 0; i < 3; i++) {
      _board[i] = new Square[3];
      for(int j = 0; j < 3; j++) {
        _board[i][j] = Square.fromChar(lines[i].charAt(j));
      }
    }
    _toMove = Square.fromChar(lines[3].charAt(0)).player();
  }
  
  public Square board(int i, int j) {
    return _board[i][j];
  }
  
  // determines terminal and score, called after board scores are set
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
        // if it's X turn, he just lost...
        _score = toMove() > 0 ? -1f : 1f;
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
    // equals requires toMove == other.toMove AND ...
    if (toMove() != other.toMove()) {
      return false;
    }
    // ... board == other.board
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
    assert validMove(m);
    Square newBoard[][] = _board.clone();
    newBoard[m.x] = _board[m.x].clone();
    newBoard[m.x][m.y] = new Square(toMove());
    
    return new State(newBoard, toMove() > 0 ? 0 : 1);
  }
  
  public State flip() {
    Square newBoard[][] = new Square[3][];
    for (int i = 0; i < 3; i++) {
      newBoard[i] = _board[i].clone();
      for(int j = 0; j < 3; j++) {
        newBoard[i][j] = _board[i][j].flip();
      }
    }
    return new State(newBoard, toMove() > 0 ? 0 : 1);
  }
  
  public String toString() {
    String output = "";
    for(int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        output += board(i,j);
      }
      output += "/";
    }
    output += new Square(toMove());
    return output;
  }
  
  public String[] toLines() {
    String lines[] = new String[0];
    return lines;
  }
}

