State state = new State(); // current state of the board
MemoryLearner memLearner = new MemoryLearner();
NetLearner netLearner = new NetLearner();

Move suggested; // the best move, as suggested by the AI
int npc = Square.X;

void setup() {
  size(640,320);
  frameRate(1000);
  
  // set mode
  setMode(netLearner, null, "NxP");
  
  // load learners
  try {
    memLearner = loadLearner("brain.json");
    netLearner = new NetLearner(loadNet("net.json"));
  } catch (Exception e) { println("Couldn't load. " + e.getMessage()); }
}

// register an attempted move by the player or AI
void moveMade(Move m) {
  // update the board
  if (m != null && state.validMove(m)) {
    state = state.updated(m);
  } else {
    println("Invalid move! " + m);
  }
  suggested = memLearner.query(state.normalize(state.toMove()));
}

void mainLoop() {
  if (state.terminal()) {
    if (state.score(Square.X) == 0.0) {
      print("T");
    } else if(state.score(Square.X) > 0) {
      print("X");
    } else {
      print("O");
    }
    // feedback to learners
    for (int i = 0; i < 2; i++) {
      if (players[i] != null) {
        players[i].feedback(state.score(i));
      }
    }
    // start new game
    state = new State();
  } else {
    int active = state.toMove();
    if (players[active] == null) {
      // wait for user to input move
    } else {
      // get a move from the current player
      Move m = players[active].play(state.normalize(active));
      if (!state.validMove(m)) {
        m = state.randomMove();
      }
      
      // TODO: remove later
      Move memMove = memLearner.play(state.normalize(state.toMove()));
      netLearner.teach(state.normalize(state.toMove()), memMove);
      
      // inform all other players of the move
      // HACK: just does 1 - active; for sake of generality,
      // should do all player where player != active
      if (players[1 - active] != null) {
        players[1 - active].moveMade(state.normalize(1 - active), m);
      }
      
      // update the board
      moveMade(m);
    }
  }
}
