State state;
ArrayList<State> path;
Move suggested;

void setup() {
  size(320,320);
  //frameRate(10);
  path = new ArrayList<State>();
  state = State.init();
}

boolean npc = true;
Learner learner = new Learner();
void draw() {
  background(255);
  fill(0);
  drawState(state, 10, 10, 100);
  if (!state.terminal()) {
    if (state.toMove() == npc || autopilot) {
      Move move = learner.play(state);
      if (move != null) {
        moveMade(move);
      } else {
        println("No move chosen!");
      }
    }
  } else {
    if (state.value() == 0.0) {
      println("Tie!");
    } else if(state.value() > 0) {
      println("X wins!");
    } else {
      println("O wins!");
    }
    learner.learn(path.toArray(new State[0]));
    path = new ArrayList<State>();
    state = State.init();
    path.add(state);
  }
}

void moveMade(Move m) {
  if (state.validMove(m)) {
    State newState = state.updated(m);
    //learner.learn(state, newState);
    state = newState;
    path.add(state);
    suggested = learner.play(state);
  } else {
    println("Invalid move! " + m);
  }
}

public void drawState(State state, float x, float y, float size) {
  // board lines change color upon victory
  stroke(0);
  if (state.value() != 0.0) {
    stroke(state.value() > 0.0 ? color(255, 0, 0) : color(0, 0, 255));
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
        boolean xo = state.board(i,j).player();
        stroke(xo == Square.X ? color(255, 0, 0) : color(0, 0, 255));
        fill(xo == Square.X ? color(255, 0, 0) : color(0, 0, 255));
        text(xo == Square.X ? "X" : "O", x + size * (i + 0.5), y + size * (j + 0.5));
      }
    }
  }
  
  // draw the cursor
  ellipseMode(CENTER);
  noStroke();
  fill(state.toMove() ? color(255, 0, 0, 125) : color(0, 0, 255, 125));
  ellipse(x + size * (cursor.x + 0.5), y + size * (cursor.y + 0.5), size/2, size/2);
  
  // show suggestion
  fill(0, 255, 0, 125);
  if (suggested != null)
    rect(x + size * suggested.x, y + size * suggested.y, size, size);
}
