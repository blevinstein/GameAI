Move cursor = new Move();
boolean autopilot = false;
boolean versus = false;

// NOTE: Controls:
// - arrows to move cursor
// - spacebar to make a move
// - S to save brain, L to load brain
// - A to toggle autopilot
// - V to toggle versus (MemoryLearner vs NetLearner)

void keyPressed() {
  switch(key) {
    case ' ':
      moveMade(cursor); break;
    case 's':
    case 'S':
      saveLearner(memLearner, "brain.json");
      saveNet(netLearner.net(), "net.json");
      break;
    case 'l':
    case 'L':
      memLearner = loadLearner("brain.json");
      netLearner = new NetLearner(loadNet("net.json"));
      break;
    case 'a':
    case 'A':
      autopilot = !autopilot; break;
    case 'v':
    case 'V':
      versus = !versus; break;
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
