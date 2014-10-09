package com.blevinstein.tictactoe;

import com.blevinstein.game.AbstractState;
import com.blevinstein.util.Util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

// Represents the state of the board in Tic Tac Toe.

public class T3State extends AbstractState<T3State,T3Move> {
  static int winners[][][] = {{{0,0},{1,1},{2,2}},
                     {{0,2},{1,1},{2,0}},
                     {{0,0},{0,1},{0,2}},
                     {{1,0},{1,1},{1,2}},
                     {{2,0},{2,1},{2,2}},
                     {{0,0},{1,0},{2,0}},
                     {{0,1},{1,1},{2,1}},
                     {{0,2},{1,2},{2,2}}};

  private T3Square[][] _board;
  private int _toMove;
  private boolean _terminal = false;
  private double _score = 0f;
  private boolean _normalized = false;
  
  public boolean terminal() { return _terminal; }
  public int toMove() { return _toMove; }
  
  public double score(int player) { return player == T3Square.X ? _score : -_score; }
  
  public T3State() {
    this(emptyBoard(), Math.random() < 0.5 ? T3Square.X : T3Square.O);
  }
  public T3State(T3Square[][] b, int tm) {
    this(b, tm, false);
  }
  public T3State(T3Square[][] b, int tm, boolean normed) {
    _board = b;
    _toMove = tm;
    _normalized = normed;
    check();
  }
  
  public static T3Square[][] emptyBoard() {
    T3Square row[] = new T3Square[3]; Arrays.fill(row, new T3Square());
    T3Square newBoard[][] = new T3Square[3][]; Arrays.fill(newBoard, row);
    return newBoard;
  }
  
  // creates a state from a string representation
  // e.g. "XX /O  / O /X", last character is toMove
  public T3State(String str) {
    String lines[] = str.split("/");
    _board = new T3Square[3][];
    for(int i = 0; i < 3; i++) {
      _board[i] = new T3Square[3];
      for(int j = 0; j < 3; j++) {
        _board[i][j] = T3Square.fromChar(lines[i].charAt(j));
      }
    }
    _toMove = T3Square.fromChar(lines[3].charAt(0)).player();
    check();
  }
  
  public T3Square board(int i, int j) {
    return _board[i][j];
  }
  
  // determines terminal and score, called after board scores are set
  public void check() {
    // check for 3 in a row
    for (int w = 0; w < winners.length; w++) {
      boolean gameWon = true;
      for (int i = 0; i < 3; i++) {
        T3Square sq = board(winners[w][i][0],winners[w][i][1]);
        if (sq.isEmpty() || sq.player() == toMove())
          gameWon = false;
      }
      if (gameWon) {
        _terminal = true;
        // if it's X turn, he just lost...
        _score = toMove() == T3Square.X ? -1f : 1f;
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
  
  public boolean equals(T3State other) {
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
  
  public T3Move[] moves() {
    ArrayList<T3Move> m = new ArrayList<T3Move>();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
       T3Move move = new T3Move(i, j);
        if (validMove(move)) {
          m.add(move);
        }
      }
    }
    return m.toArray(new T3Move[0]);
  }
  public boolean validMove(T3Move m) {
    return board(m.x(),m.y()).isEmpty();
  }
  
  public T3State clone() {
    T3Square newBoard[][] = _board.clone();
    return new T3State(newBoard, toMove());
  }
  
  public T3State updated(T3Move m) {
    if (!validMove(m)) throw new IllegalArgumentException("Invalid move: " + m);
    T3Square newBoard[][] = _board.clone();
    newBoard[m.x()] = _board[m.x()].clone();
    newBoard[m.x()][m.y()] = new T3Square(toMove());
    return new T3State(newBoard, 1 - toMove(), _normalized);
  }

  public T3State normalize(int player) {
    if (player > 0) {
      T3State s = flip();
      s._normalized = true;
      return s;
    } else {
      T3State s = clone();
      s._normalized = true;
      return s;
    }
  }
  
  public boolean normalized() {
    return _normalized;
  }
  
  // switches Xs and Os
  public T3State flip() {
    T3Square newBoard[][] = new T3Square[3][];
    for (int i = 0; i < 3; i++) {
      newBoard[i] = _board[i].clone();
      for(int j = 0; j < 3; j++) {
        newBoard[i][j] = _board[i][j].flip();
      }
    }
    return new T3State(newBoard, 1 - toMove());
  }
  
  // see T3Move(String) above
  public String toString() {
    String output = "";
    for(int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        output += board(i,j);
      }
      output += "/";
    }
    output += new T3Square(toMove());
    return output;
  }
  
  // each board square is represented as two bits
  public double[] toDoubles() {
    double vector[] = new double[18];
    // currently, iterates over i and j, calculating index
    // could also iterate over bits 0-17 and calculate i,j from index
    for(int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        // input index determined by i, j, x/o
        int xIdx = (3*i + j) * 2;
        int oIdx = xIdx + 1;
        vector[xIdx] = 0.0;
        vector[oIdx] = 0.0;
        if (!board(i,j).isEmpty()) {
          if (board(i,j).player() == T3Square.X) {
            vector[xIdx] = 1.0;
          } else {
            vector[oIdx] = 1.0;
          }
        }
      }
    }
    return vector;
  }
  
  void draw(Graphics g, int x, int y, int size) {
    draw(g, x, y, size, null, null);
  }
  
  void draw(Graphics g, int x, int y, int size, T3Move cursor, T3Move suggested) {
    int side = size / 3;
    
    // board lines change color upon victory
    g.setColor(Color.BLACK);
    if (score(T3Square.X) != 0.0) {
      g.setColor(score(T3Square.X) > 0.0 ? Color.RED : Color.BLUE);
    }
    
    // draw the board
    for (int i = 0; i <= 3; i++) {
      g.drawLine(x + side*i, y, x + side*i, y + side*3);
      g.drawLine(x, y + side*i, x + side*3, y + side*i);
    }
    
    // font handling
    g.setFont(new Font("Arial", Font.PLAIN, side / 3));
    
    // draw the marks
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (!board(i, j).isEmpty()) {
          // draw an X or O, centered in the square
          if (board(i,j).player() == T3Square.X) {
            g.setColor(Color.RED);
            Util.placeText(g, Util.CENTER, "X",
                             (int)(x + side*(i+0.5)),
                             (int)(y + side*(j+0.5)));
          } else {
            g.setColor(Color.BLUE);
            Util.placeText(g, Util.CENTER, "O",
                             (int)(x + side*(i+0.5)),
                             (int)(y + side*(j+0.5)));
          }
        }
      }
    }
    
    // draw the cursor
    if (cursor != null) {
      if (toMove() == T3Square.X) {
        g.setColor(new Color(255, 0, 0, 125));
      } else {
        g.setColor(new Color(0, 0, 255, 125));
      }
      g.fillOval((int)(x + side * (cursor.x() + 0.5) - side / 4),
                 (int)(y + side * (cursor.y() + 0.5) - side / 4),
                 side / 2, side / 2);
    }
    
    // show suggestion
    if (suggested != null) {
      g.setColor(new Color(0, 255, 0, 125));
      g.fillRect(x + side * suggested.x(),
                 y + side * suggested.y(),
                 side, side);
    }
  }
}

