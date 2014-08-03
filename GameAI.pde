import java.awt.Graphics;

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
    HashMap map = Json.loadMap("brain.json");
    if (map != null) memLearner = new MemoryLearner(map);
    
    NeuralNet net = Json.loadNet("net.json");
    if (net != null) netLearner = new NetLearner(net);
    
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

void draw() {
  // execute game loop
  mainLoop();
  
  // clear the screen
  background(255);
  
  // draw the board
  game.state().draw(graphics(), 10, 10, 300, cursor, suggested);
  
  // draw the neural network's thoughts
  netLearner.drawThoughts(graphics(), game.state(), 330, 10, 300);
  
  // show frameRate and mode
  stroke(0); fill(0);
  textAlign(RIGHT, TOP);
  textSize(100 / 4);
  text((int)frameRate + " FPS, Mode " + modeStr, width-10, 10);
}

// NOTE: Using this.g.image.getGraphics() as suggested here
// http://forum.processing.org/one/topic/drawing-to-papplet-getgraphics-vs-papplet-g-image-getgraphics.html
Graphics graphics() {
  return this.g.image.getGraphics();
}
