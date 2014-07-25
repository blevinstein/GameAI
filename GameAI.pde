State state = new State(); // current state of the board

ArrayList<State> path = new ArrayList<State>(); // all moves made this game

Learner learner = new Learner(Math.random() < 0.5 ? Square.X : Square.O);

Move suggested; // the best move, as suggested by the AI

void setup() {
  size(640,320);
  frameRate(1000);
}

void draw() {
  // clear the screen
  background(255);
  
  // draw the board
  drawState((State)state, 10, 10, 100);
  
  // game loop:
  if (!state.terminal()) {
    // game has not ended
    if (state.toMove() == learner.player() || autopilot) {
      // AI makes a move
      moveMade(learner.play(normalize(state)));
    }
  } else {
    if (state.score(0) == 0.0) {
      println("Tie!");
    } else if(state.score(0) > 0) {
      println("X wins!");
    } else {
      println("O wins!");
    }
    // learn from all moves made in this game
    // HACK: passing toArray() a zero-element State[] for typing reasons
    learner.learn(path.toArray(new State[0]));
    // start a new game
    state = new State();
    // reset the list of moves made
    path = new ArrayList<State>();
    path.add(state);
  }
}

// register a move made by the player or AI
void moveMade(Move m) {
  if (m != null && state.validMove(m)) {
    state = state.updated(m);
    path.add(state);
    if (!autopilot) {
      suggested = learner.play(normalize(state));
    } else {
      suggested = null;
    }
  } else {
    println("Invalid move! " + m);
  }
}

// the learner only knows how to play as one side,
// so we have to flip the board manually
State normalize(State s) {
  return s.toMove() == learner.player() ? s : s.flip();
}

void drawState(State state, float x, float y, float size) {
  // board lines change color upon victory
  stroke(0);
  if (state.score(0) != 0.0) {
    stroke(state.score(0) > 0.0 ? color(255, 0, 0) : color(0, 0, 255));
  }
  
  // draw the board
  for (int i = 0; i <= 3; i++) {
    line(x + size*i, y, x + size*i, y + size*3);
    line(x, y + size*i, x + size*3, y + size*i);
  }
  
  // draw the marks
  textSize(size/4);
  textAlign(CENTER, CENTER);
  for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
      if (!state.board(i, j).isEmpty()) {
        int xo = state.board(i,j).player();
        stroke(xo == Square.X ? color(255, 0, 0) : color(0, 0, 255));
        fill(xo == Square.X ? color(255, 0, 0) : color(0, 0, 255));
        text(xo == Square.X ? "X" : "O", x + size * (i + 0.5), y + size * (j + 0.5));
      }
    }
  }
  
  // show frameRate
  stroke(0); fill(0);
  textAlign(RIGHT, TOP);
  text((int)frameRate + " FPS", width-10, 10);
  
  // draw the cursor
  ellipseMode(CENTER);
  noStroke();
  fill(state.toMove() > 0 ? color(255, 0, 0, 125) : color(0, 0, 255, 125));
  ellipse(x + size * (cursor.x + 0.5), y + size * (cursor.y + 0.5), size/2, size/2);
  
  // show suggestion
  fill(0, 255, 0, 125);
  if (suggested != null)
    rect(x + size * suggested.x, y + size * suggested.y, size, size);
}
