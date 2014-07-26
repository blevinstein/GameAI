State state = new State(); // current state of the board

ArrayList<State> path = new ArrayList<State>(); // all moves made this game

MemoryLearner memLearner = new MemoryLearner(Math.random() < 0.5 ? Square.X : Square.O);
NetLearner netLearner = new NetLearner();

Move suggested; // the best move, as suggested by the AI

void setup() {
  size(640,320);
  frameRate(1000);
  
  // load learners
  try {
    memLearner = loadLearner("brain.json");
    netLearner = new NetLearner(loadNet("net.json"));
  } catch (Exception e) { println("Couldn't load. " + e.getMessage()); }
}

void draw() {
  /*
  if (teaching)
    frameRate(10);
  else
    frameRate(1000);
  //*/
  
  // clear the screen
  background(255);
  
  //if (teaching) {
  //  teachRandom(memLearner, netLearner);
  //}
  
  // draw the board
  drawState((State)state, 10, 10, 100);
  
  // draw the neural network's thoughts
  drawThoughts(netLearner, 330, 10, 100);
  
  // show frameRate
  stroke(0); fill(0);
  textAlign(RIGHT, TOP);
  textSize(100 / 4);
  text((int)frameRate + " FPS", width-10, 10);
  
  // game loop:
  if (!state.terminal()) {
    // game has not ended
    if (state.toMove() == memLearner.player() || autopilot) {
      // neural network makes a move
      Move netMove = netLearner.play(normalize(state));
      if (!state.validMove(netMove)) {
        print("X");
      }
      // learn from the memLearner
      netLearner.learn(state, memLearner.play(normalize(state)));
      moveMade(state.validMove(netMove) ? netMove : state.randomMove());
    } else if (versus) {
      Move m = memLearner.play(normalize(state));
      // learn from the memLearner when it moves
      netLearner.learn(state, m);
      moveMade(m);
    }
  } else {
    if (state.score(0) == 0.0) {
      print("\nTie   ");
    } else if(state.score(0) > 0) {
      print("\nX win ");
    } else {
      print("\nO win ");
    }
    // learn from all moves made in this game
    // HACK: passing toArray() a zero-element State[] for typing reasons
    memLearner.learn(path.toArray(new State[0]));
    // start a new game
    state = new State();
    // reset the list of moves made
    path = new ArrayList<State>();
    path.add(state);
  }
}

void drawThoughts(NetLearner learner, float x, float y, float size) {
  double input[] = state.toDoubles();
  double output[] = netLearner.net().process(input);
  // get max magnitude
  double max = 0;
  for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
      double value = output[i*3+j];
      if (Math.abs(value) > max) max = value;
    }
  }
  stroke(0);
  for (int i = 0; i < 3; i++) {
    for(int j = 0; j < 3; j++) {
      fill((int)(output[i*3+j] / Math.abs(max) * 255), 125);
      rect(x + i * size, y + j * size, size, size);
    }
  }
  
  // print weights around the board
  fill(0);
  textSize(size / 8);
  for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
      text(String.format("%.2f", output[i*3+j]), x + size * (i + 0.5), y + size * (j + 0.5));
    }
  }
}

void teachRandom(MemoryLearner teacher, NetLearner learner) {
  ArrayList<String> stateStrings = new ArrayList<String>(teacher.states());
  if (stateStrings.size() == 0) return;
  State s = null;
  while (s == null || s.terminal()) {
    String str = stateStrings.get((int)(Math.random() * stateStrings.size()));
    s = new State(str);
  }
  Move m = teacher.play(normalize(s));
  assert m != null;
  learner.learn(s, m);
  
  // calculate error
  /*
  double input[] = s.toDoubles();
  double actual[] = learner.net().process(input);
  double target[] = m.toDoubles();
  double error = 0;
  for (int i = 0; i < actual.length; i++) {
    double diff = actual[i] - target[i];
    error += diff * diff;
  }
  print("error " + String.format("%.4f", Math.sqrt(error)) + " ");
  //*/
}

// register a move made by the player or AI
void moveMade(Move m) {
  if (m != null && state.validMove(m)) {
    state = state.updated(m);
    path.add(state);
    if (autopilot) {
      suggested = null;
    } else {
      suggested = memLearner.play(normalize(state));
    }
  } else {
    println("Invalid move! " + m);
  }
}

// the learner only knows how to play as one side,
// so we have to flip the board manually
State normalize(State s) {
  return s.toMove() == memLearner.player() ? s : s.flip();
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
