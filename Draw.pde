
void draw() {
  // execute game loop
  mainLoop();
  
  // clear the screen
  background(255);
  
  // draw the board
  // NOTE: Using this.g.image.getGraphics() as suggested here
  // http://forum.processing.org/one/topic/drawing-to-papplet-getgraphics-vs-papplet-g-image-getgraphics.html
  state.draw(this.g.image.getGraphics(), 10, 10, 300, cursor, suggested);
  
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
