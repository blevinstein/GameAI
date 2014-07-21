int cursorx = 0, cursory = 0;
boolean autopilot = false;

void keyPressed() {
  switch(key) {
    case ' ':
      moveMade(cursorx, cursory); break;
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
      if (cursory > 0) cursory--; break;
    case DOWN:
      if (cursory < 2) cursory++; break;
    case LEFT:
      if (cursorx > 0) cursorx--; break;
    case RIGHT:
      if (cursorx < 2) cursorx++; break;
  }
}
