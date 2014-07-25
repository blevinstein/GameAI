State state;
ArrayList<State> path;
Move suggested;

void setup() {
  size(640,320);
  frameRate(1000);
  path = new ArrayList<State>();
  state = new State();
}

Learner learner = new Learner(Math.random() < 0.5 ? Square.X : Square.O);
void draw() {
  background(255);
  fill(0);
  drawState((State)state, 10, 10, 100);
  if (!state.terminal()) {
    if (state.toMove() == learner.player() || autopilot) {
      Move move = learner.play(normalize(state));
      if (move != null) {
        moveMade(move);
      } else {
        println("No move chosen!");
      }
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
    learner.learn(path.toArray(new State[0]));
    path = new ArrayList<State>();
    state = new State();
    path.add(state);
  }
}

void moveMade(Move m) {
  if (state.validMove(m)) {
    State newState = state.updated(m);
    // NOTE: learns from a full path at the end, not after each move
    // e.g. learner.learn(state, newState);
    state = newState;
    path.add(state);
    suggested = learner.play(normalize(state));
  } else {
    println("Invalid move! " + m);
  }
}

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
  
  stroke(0); fill(0);
  textAlign(RIGHT, TOP);
  text(frameRate, width-10, 10);
  
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
