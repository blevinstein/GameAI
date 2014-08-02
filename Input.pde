T3Move cursor = new T3Move();
boolean versus = false;

// NOTE: Controls:
// - arrows to move cursor
// - spacebar to make a move
// - S to save brain, L to load brain
// - M to change mode

AbstractLearner players[] = {netLearner, null};
String modeStr = "";
int mode = 0;

void keyPressed() {
  switch(key) {
    case ' ':
      moveMade(cursor); break;
    case 's':
    case 'S':
      println();
      saveLearner(memLearner, "brain.json");
      saveNet(netLearner.net(), "net.json");
      break;
    case 'l':
    case 'L':
      println();
      memLearner = loadLearner("brain.json");
      netLearner = new NetLearner(loadNet("net.json"));
      break;
    case 'm':
    case 'M':
      // different "game modes"
      mode = (mode + 1) % 4;
      println();
      switch (mode) {
        case 0: setMode(netLearner, null, "NxP"); break;
        case 1: setMode(memLearner, memLearner, "MxM"); break;
        case 2: setMode(netLearner, netLearner, "NxN"); break;
        case 3: setMode(netLearner, memLearner, "NxM"); break;
        case 4: setMode(memLearner, null, "MxP"); break;
      }
      break;
    case 'u':
    case 'U':
      println();
      println("X " + wins[T3Square.X] + " O " + wins[T3Square.O]);
      break;
  }
  switch(keyCode) {
    case UP:
      if (cursor.y > 0) cursor.y--; break;
    case DOWN:
      if (cursor.y < 2) cursor.y++; break;
    case LEFT:
      if (cursor.x > 0) cursor.x--; break;
    case RIGHT:
      if (cursor.x < 2) cursor.x++; break;
  }
}

void setMode(AbstractLearner a, AbstractLearner b, String str) {
  players[0] = a;
  players[1] = b;
  modeStr = str;
}
