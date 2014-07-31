abstract class AbstractLearner {
  public abstract Move query(AbstractState s);
  
  // explicitly teach to make move M in state S
  public abstract void teach(AbstractState s, Move m);
  
  // query and remember move
  public abstract Move play(AbstractState s);
  
  // informs the learner about moves made by other player(s)
  public abstract void moveMade(AbstractState s, Move m);
  
  // give positive or negative feedback
  public abstract void feedback(double feedback);
}
