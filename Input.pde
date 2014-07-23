Move cursor = new Move();
boolean autopilot = false;

void keyPressed() {
  switch(key) {
    case ' ':
      moveMade(cursor); break;
    case 's':
    case 'S':
      learner.saveJSON("brain.json"); break;
    case 'l':
    case 'L':
      learner.loadJSON("brain.json"); break;
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
