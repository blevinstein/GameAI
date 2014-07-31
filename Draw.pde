
void draw() {
  // execute game loop
  mainLoop();
  
  // clear the screen
  background(255);
  
  // draw the board
  drawState((State)state, 10, 10, 100);
  
  // draw the neural network's thoughts
  drawThoughts(netLearner, 330, 10, 100);
  
  // show frameRate and mode
  stroke(0); fill(0);
  textAlign(RIGHT, TOP);
  textSize(100 / 4);
  text((int)frameRate + " FPS, Mode " + modeStr, width-10, 10);
  
  // show memLearner values
  /*
  stroke(0); fill(0);
  textAlign(RIGHT, TOP);
  textSize(100 / 4);
  text(memLearner.value(state.normalize(0)), width-10, 100);
  text(memLearner.value(state.normalize(1)), width-10, 200);
  */
}

void drawThoughts(NetLearner learner, float x, float y, float size) {
  double input[] = state.toDoubles();
  double output[] = netLearner.net().process(input);
  // get max magnitude
  double max = 0;
  for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
      double value = output[i*3+j];
      if (Math.abs(value) > max) max = value;
    }
  }
  stroke(0);
  for (int i = 0; i < 3; i++) {
    for(int j = 0; j < 3; j++) {
      fill((int)(output[i*3+j] / Math.abs(max) * 255), 125);
      rect(x + i * size, y + j * size, size, size);
    }
  }
  
  // print weights around the board
  fill(0);
  textSize(size / 8);
  for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
      text(String.format("%.2f", output[i*3+j]), x + size * (i + 0.5), y + size * (j + 0.5));
    }
  }
}

void drawState(State state, float x, float y, float size) {
  // board lines change color upon victory
  stroke(0);
  if (state.score(0) != 0.0) {
    stroke(state.score(Square.X) > 0.0 ? color(255, 0, 0) : color(0, 0, 255));
  }
  
  // draw the board
  for (int i = 0; i <= 3; i++) {
    line(x + size*i, y, x + size*i, y + size*3);
    line(x, y + size*i, x + size*3, y + size*i);
  }
  
  // draw the marks
  textSize(size/4);
  textAlign(CENTER, CENTER);
  for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
      if (!state.board(i, j).isEmpty()) {
        int xo = state.board(i,j).player();
        stroke(xo == Square.X ? color(255, 0, 0) : color(0, 0, 255));
        fill(xo == Square.X ? color(255, 0, 0) : color(0, 0, 255));
        text(xo == Square.X ? "X" : "O", x + size * (i + 0.5), y + size * (j + 0.5));
      }
    }
  }
  
  // draw the cursor
  ellipseMode(CENTER);
  noStroke();
  fill(state.toMove() == Square.X ? color(255, 0, 0, 125) : color(0, 0, 255, 125));
  ellipse(x + size * (cursor.x + 0.5), y + size * (cursor.y + 0.5), size/2, size/2);
  
  // show suggestion
  fill(0, 255, 0, 125);
  if (suggested != null) {
    rect(x + size * suggested.x, y + size * suggested.y, size, size);
  }
}
