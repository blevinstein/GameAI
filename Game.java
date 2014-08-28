import java.util.ArrayList;

// Represents a game of Tic Tac Toe.
//
// Can be advanced one move (step), or played until completion or IO block
// (play). Moves are made by calling moveMade, which assumes the correct
// player (toMove) is acting.

class Game {
  private T3State _state;
  public T3State state() { return _state; }
  private ArrayList<Learner<T3State,T3Move>> players;

  public Game() {
    this(null, null);
  }
  public Game(Learner<T3State,T3Move> p) {
    this(p, null);
  }
  public Game(Learner<T3State,T3Move> p1, Learner<T3State,T3Move> p2) {
    _state = new T3State();
    players = new ArrayList<Learner<T3State,T3Move>>();
    players.add(p1);
    players.add(p2);
  }
  
  // returns next player to move
  public int toMove() {
    return done() ? -1 : _state.toMove();
  }
  public boolean done() {
    return _state.terminal();
  }
  
  // plays moves until done or until waiting for user input
  // returns number of moves made
  public int play() {
    int movesMade = 0;
    while (canStep()) {
      step();
      movesMade++;
    }
    return movesMade;
  }
  
  // returns true if step() can be executed
  // returns false if game is over
  // returns false if waiting on user input
  public boolean canStep() {
    return !done() && players.get(toMove()) != null;
  }
  
  public void step() {
    if (!canStep()) throw new IllegalArgumentException("Cannot make move.");
    
    Learner<T3State,T3Move> player = players.get(toMove());
    T3Move m = player.play(_state.normalize(toMove()));
    for (int i = 0; i < players.size(); i++) {
      if (i == toMove()) continue; // don't tell players about their own moves
      if (players.get(i) == null) continue; // skip null players (users)
      if (!_state.normalize(i).validMove(m))
        throw new IllegalArgumentException("Invalid move.");
      players.get(i).moveMade(_state.normalize(i), m);
    }
    moveMade(m);

    feedback();
  }
  
  // if applicable, give feedback to players
  private void feedback() {
    if (done()) {
      for (int i = 0; i < 2; i++) {
        Learner<T3State,T3Move> player = players.get(i);
        if (player != null) {
          player.feedback(_state.score(i));
        }
      }
    }
  }
    
  // register an attempted move by the player or AI
  // throws an exception for invalid moves
  public void moveMade(T3Move m) {
    // update the board
    if (m != null && _state.validMove(m)) {
      _state = _state.updated(m);
    } else {
      throw new IllegalArgumentException("Invalid move: " + m);
    }
  }
  
  public int winner() {
    if (!done()) return -1;
    return _state.score(T3Square.X) > _state.score(T3Square.O) ? T3Square.X : T3Square.O;
  }
}
