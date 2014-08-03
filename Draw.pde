import java.awt.Graphics;

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
