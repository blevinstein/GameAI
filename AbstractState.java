import java.util.ArrayList;
import java.util.Arrays;

// TODO: make AbstractState<Move>
public abstract class AbstractState {
  public abstract boolean terminal();
  
  // give the score for each player, to allow a generic Learner interface
  // defaults to player 0, which is useful after normalization
  public float score() {
    //assert normalized();
    if (!normalized()) {
      throw new IllegalArgumentException("Must normalize state!");
    }
    return score(0);
  }
  public abstract float score(int player);
  
  // return all legal moves from this state
  public abstract Move[] moves();
  
  public Move randomMove() {
    Move allMoves[] = moves();
    return allMoves[(int)(Math.random() * allMoves.length)];
  }
  
  // check whether a given move
  public abstract boolean validMove(Move m);
  
  // allocates new, identical state
  public abstract AbstractState clone();
  
  // returns state after move is made
  public abstract AbstractState updated(Move m);
  
  public abstract String toString();
  public abstract double[] toDoubles();
  
  public abstract int toMove();
  
  // change state so that player 0 = p
  public abstract AbstractState normalize(int p);
  // flag to allow runtime checking of normalization
  public abstract boolean normalized();
}
