interface Learner<S extends AbstractState, M extends Move> {
  public abstract Move query(S s);
  
  // explicitly teach to make move M in state S
  public abstract void teach(S s, M m);
  
  // query and remember move
  public abstract M play(S s);
  
  // informs the learner about moves made by other player(s)
  public abstract void moveMade(S s, M m);
  
  // give positive or negative feedback
  public abstract void feedback(double feedback);
}
