State state = new State(); // current state of the board
MemoryLearner memLearner = new MemoryLearner();
NetLearner netLearner = new NetLearner();

Move suggested; // the best move, as suggested by the AI
int npc = Square.X;

int wins[] = new int[]{0, 0};

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
      wins[Square.X]++;
      print("X");
    } else {
      wins[Square.O]++;
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
      // HACK: this typecast required because I can't instantiate an AbstractLearner<State, Move>[]
      Move m = ((AbstractLearner<State, Move>)players[active]).play(state.normalize(active));
      if (!state.validMove(m)) {
        print("_");
        m = state.randomMove();
      }
      
      // inform all other players of the move
      // HACK: just does 1 - active; for sake of generality,
      // should do all player where player != active
      if (players[1 - active] != null) {
        players[1 - active].moveMade(state.normalize(active), m);
      }
      
      // update the board
      moveMade(m);
    }
  }
}
