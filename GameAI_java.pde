State state;

void setup() {
  size(400,400);
  state = initState();
}

int i;
void draw() {
  background(255);
  fill(0);
  state.draw(10, 10, 50);
}

void moveMade(int i, int j) {
  if (state.terminal) {
    state = initState();
  } else if (state.validMove(i, j)) {
    state = state.updated(i, j);
  }
}

/*
 * Player
 *   click start
 *   click destination
 *   check if move is valid
 *   check if score or state needs to change
 *
 * AI
 *   search tree of possible moves, evaluate with heuristic
 *     possible moves = each possible move for each piece
 *   later: add machine learning?
 */
