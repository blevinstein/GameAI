Move cursor = new Move();
boolean autopilot = false;

// NOTE: Controls:
// - arrows to move cursor
// - spacebar to make a move
// - S to save brain, L to load brain
// - A to toggle autopilot

void keyPressed() {
  switch(key) {
    case ' ':
      moveMade(cursor); break;
    case 's':
    case 'S':
      saveLearner(learner, "brain.json"); break;
    case 'l':
    case 'L':
      learner = loadLearner("brain.json"); break;
    case 'a':
    case 'A':
      autopilot = !autopilot;
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
