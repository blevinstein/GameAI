Game game = new Game();
MemoryLearner memLearner = new MemoryLearner();
NetLearner netLearner = new NetLearner();

AbstractLearner player1 = netLearner, player2 = null;

T3Move suggested; // the best move, as suggested by the AI

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

Game newGame() {
  return new Game(player1, player2);
}

void mainLoop() {
  if (game.done()) {
    // print winner and count wins
    switch(game.winner()) {
      case T3Square.X: wins[T3Square.X]++; print("X"); break;
      case T3Square.O: wins[T3Square.O]++; print("O"); break;
      default: print("T");
    }
    // start new game
    game = newGame();
  } else if(game.canStep()) {
    game.step();
    suggested = memLearner.query(game.state().normalize(game.toMove()));
  }
  // else, game is waiting on user input
}
