State state;

void setup() {
  size(600,300);
  state = initState();
}

int i;
boolean npc = true;
Learner learner = new Learner();
void draw() {
  background(255);
  fill(0);
  state.draw(10, 10, 100);
  if (!state.terminal) {
    if (state.toMove == npc || autopilot) {
      int move[] = learner.play(state);
      if (move != null) {
        moveMade(move[0], move[1]);
      } else {
        println("No move chosen!");
      }
    }
  } else {
    if (!state.won) {
      println("Tie!");
    } else if(state.winner()) {
      println("X wins!");
    } else {
      println("O wins!");
    }
    state = initState();
  }
}

void moveMade(int i, int j) {
  if (state.validMove(i, j)) {
    State newState = state.updated(i, j);
    learner.learn(state, newState);
    state = newState;
  } else {
    println("Invalid move! [" + i + "," + j + "]");
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
