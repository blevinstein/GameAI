import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractState {
  public abstract boolean terminal();
  
  // give the score for each player, to allow a generic Learner interface
  public abstract float score(int player);
  
  // return all legal moves from this state
  public abstract Move[] moves();
  
  // check whether a given move
  public abstract boolean validMove(Move m);
  
  // allocates new, identical state
  public abstract AbstractState clone();
  
  // returns state after move is made
  public abstract AbstractState updated(Move m);
  
  public abstract String toString();
  
  public abstract int toMove();
}
