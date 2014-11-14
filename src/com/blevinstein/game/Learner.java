package com.blevinstein.game;

// Represents a "learner", who attempts to come up with the best move for a
// given state, and can be taught by direct example:
//
// teach(s, m) // "the correct move in state s is move m"
//
// or by giving feedbck:
//
// while(!done) {
//   move = query(state)
//   // update state
// }
// feedback(value of state) // positive for good, negative for bad
//
// although some implementations may only learn from one of the two methods.

public interface Learner<S extends AbstractState<S, M>, M extends Move> {
  public abstract M query(S s);

  // explicitly teach to make move M in state S
  public abstract void teach(S s, M m);

  // query and remember move
  public abstract M play(S s);

  // informs the learner about moves made by other player(s)
  public abstract void moveMade(S s, M m);

  // give positive or negative feedback
  public abstract void feedback(double feedback);
}
