ArrayList<PVector> moveBuffer = new ArrayList<PVector>();

int cursorx = 0, cursory = 0;

void keyPressed() {
  switch(keyCode) {
    case UP:
      if (cursory > 0) cursory--; break;
    case DOWN:
      if (cursory < 2) cursory++; break;
    case LEFT:
      if (cursorx > 0) cursorx--; break;
    case RIGHT:
      if (cursorx < 2) cursorx++; break;
    case ENTER:
      moveMade(cursorx, cursory);
  }
}
