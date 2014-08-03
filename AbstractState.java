import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractState<M extends AbstractMove, Self> {
  public abstract boolean terminal();
  
  // give the score for each player, to allow a generic Learner interface
  // defaults to player 0, which is useful after normalization
  public double score() {
    if (!normalized()) {
      throw new IllegalArgumentException("Must normalize state!");
    }
    return score(0);
  }
  public abstract double score(int player);
  
  // return all legal moves from this state
  public abstract M[] moves();
  
  public M randomMove() {
    if (terminal()) return null; // no valid moves
    M allMoves[] = moves();
    return allMoves[(int)(Math.random() * allMoves.length)];
  }
  
  // check whether a given move
  public abstract boolean validMove(M m);
  
  // allocates new, identical state
  public abstract Self clone();
  
  // returns state after move is made
  public abstract Self updated(M m);
  
  public abstract String toString();
  public abstract double[] toDoubles();
  
  public abstract int toMove();
  
  // change state so that player 0 = p
  public abstract Self normalize(int p);
  // flag to allow runtime checking of normalization
  public abstract boolean normalized();
}
