int winners[][][] = {{{0,0},{1,1},{2,2}},
                     {{0,2},{1,1},{2,0}},
                     {{0,0},{0,1},{0,2}},
                     {{1,0},{1,1},{1,2}},
                     {{2,0},{2,1},{2,2}},
                     {{0,0},{1,0},{2,0}},
                     {{0,1},{1,1},{2,1}},
                     {{0,2},{1,2},{2,2}}};

class State {
  public Square[][] board;
  public boolean toMove;
  public boolean terminal = false;
  public boolean won = false;
  
  public State(Square[][] b, boolean tm) {
    board = b;
    toMove = tm;
    
    // check for termination
    boolean gameFull = true;
    for (int w = 0; w < winners.length; w++) {
      boolean gameWon = true;
      for (int i = 0; i < 3; i++) {
        Square sq = board[winners[w][i][0]][winners[w][i][1]];
        if (!sq.full || sq.player == toMove)
          gameWon = false;
        if (!sq.full)
          gameFull = false;
      }
      if (gameWon) {
        terminal = true;
        won = true;
      }
    }
    if (gameFull) {
      terminal = true;
    }
  }
  
  public boolean equals(State other) {
    if (toMove != other.toMove) {
      return false;
    }
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (board[i][j] != other.board[i][j])
          return false;
      }
    }
    return true;
  }
  
  public int[][] moves() {
    int m[][] = new int[0][];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (validMove(i, j)) {
          m = (int[][])append(m, new int[]{i, j});
        }
      }
    }
    return m;
  }
  
  public boolean validMove(int i, int j) {
    return board[i][j].equals(empty());
  }
  
  public State clone() {
    Square newBoard[][] = new Square[3][];
    arrayCopy(board, newBoard);
    return new State(newBoard, toMove);
  }
  
  public State updated(int i, int j) {
    Square newBoard[][] = new Square[3][];
    arrayCopy(board, newBoard);
    newBoard[i] = new Square[3];
    arrayCopy(board[i], newBoard[i]);
    newBoard[i][j] = mark(toMove);
    
    return new State(newBoard, !toMove);
  }
  
  public boolean winner() {
    return !toMove;
  }
  
  public String toString() {
    String output = "";
    for(int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        output += board[i][j];
      }
      output += "/";
    }
    output += mark(toMove);
    return output;
  }
  
  public String[] toLines() {
    String lines[] = new String[0];
    return lines;
  }
  
  public void draw(float x, float y, float size) {
    stroke(0);
    if (won) {
      stroke(winner() ? color(255, 0, 0) : color(0, 0, 255));
    }
    for (int i = 0; i <= 3; i++) {
      line(x + size*i, y, x + size*i, y + size*3);
      line(x, y + size*i, x + size*3, y + size*i);
    }
    textSize(size/4);
    textAlign(CENTER, CENTER);
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (board[i][j].full) {
          boolean xo = board[i][j].player;
          stroke(xo ? color(255, 0, 0) : color(0, 0, 255));
          fill(xo ? color(255, 0, 0) : color(0, 0, 255));
          text(xo ? "X" : "O", x + size * (i + 0.5), y + size * (j + 0.5));
        }
      }
    }
    ellipseMode(CENTER);
    noStroke();
    fill(state.toMove ? color(255, 0, 0, 125) : color(0, 0, 255, 125));
    ellipse(x + size * (cursorx + 0.5), y + size * (cursory + 0.5), size/2, size/2);
  }
}

public State initState() {
  return new State(new Square[][]{{empty(), empty(), empty()},
                                  {empty(), empty(), empty()},
                                  {empty(), empty(), empty()}}, random(1.0) < 0.5);
}

public State fromString(String str) {
  String lines[] = split(str, '/');
  Square newBoard[][] = new Square[3][];
  for(int i = 0; i < 3; i++) {
    newBoard[i] = new Square[3];
    for(int j = 0; j < 3; j++) {
      newBoard[i][j] = fromChar(lines[i].charAt(j));
    }
  }
  boolean toMove = fromChar(lines[3].charAt(0)).player;
  return new State(newBoard, toMove);
}

