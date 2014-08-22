import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.RealMatrix;

// represents knowledge with a neural network
class NetLearner implements Learner<T3State, T3Move> {
  private double EPSILON = 0.0;
  
  private NeuralNet _net;
  public NeuralNet net() { return _net; }
  
  private ArrayList<double[][]> myMoves = new ArrayList<double[][]>();
  private ArrayList<double[][]> otherMoves = new ArrayList<double[][]>();
  
  public NetLearner() {
    this(new NeuralNet(new int[]{18, 14, 9}));
  }
  public NetLearner(NeuralNet n) {
    assert n != null;
    _net = n;
  }
  
  // choose a move
  public T3Move query(T3State s) {
    // with some probability, choose randomly
    if (Math.random() < EPSILON) {
      return s.randomMove();
    }
    
    double input[] = s.toDoubles();
    double output[] = _net.process(input);

    int idx = Util.choose(output);
    T3Move m = new T3Move(idx/3, idx%3);
    // replace invalid moves wih random moves
    if (!s.validMove(m)) return s.randomMove();
    return m;
  }
  
  // query and save input => output results
  public T3Move play(T3State s) {
    double input[] = s.toDoubles();
    T3Move m = query(s);
    
    // save input => output
    double move[][] = new double[2][];
    move[0] = input;
    move[1] = m.toDoubles();
    myMoves.add(move);
    
    return m;
  }
  
  // remembers moves by other player, for training purposes
  public void moveMade(T3State s, T3Move m) {
    double move[][] = new double[2][];
    move[0] = s.toDoubles();
    move[1] = m.toDoubles();
    otherMoves.add(move);
  }
  
  // learn by explicit assertions about the correct move in a given state
  public void teach(T3State s, T3Move m) {
    double input[] = s.toDoubles();
    double target[] = m.toDoubles();
    _net.backpropagate(input, target);
  }
  
  public void teach(ArrayList<double[][]> moves) {
    for (int i = 0; i < moves.size(); i++) {
      double input[] = moves.get(i)[0];
      double output[] = moves.get(i)[1];
      _net.backpropagate(input, output);
    }
  }
  
  // gives positive or negative feedback to the network
  public void feedback(double f) {
    if (f > 0.0) {
      teach(myMoves);
    } else if (f < 0.0) {
      teach(otherMoves);
    }
    forget();
  }
  
  // forget remembered input => output results
  public void forget() {
    myMoves.clear();
    otherMoves.clear();
  }
  
  public void drawThoughts(Graphics g, T3State s, int x, int y, int size) {
    int side = size / 3;
    double input[] = s.toDoubles();
    double output[] = net().process(input);
    // get max magnitude
    double max = 0;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        double value = output[i*3+j];
        if (Math.abs(value) > max) max = value;
      }
    }
    
    // draw each square
    for (int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        int gray = (int)(output[i*3+j] / Math.abs(max) * 255);
        g.setColor(new Color(gray, gray, gray, 125));
        g.fillRect(x + i * side, y + j * side, side, side);
        g.setColor(Color.BLACK);
        g.drawRect(x + i * side, y + j * side, side, side);
      }
    }
    
    // print weights around the board
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.PLAIN, side / 6));
    FontMetrics fm = g.getFontMetrics();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        String str = String.format("%.2f", output[i*3+j]);
        g.drawString(str,
                     (int)(x + side * (i + 0.5) - fm.stringWidth(str) / 2),
                     (int)(y + side * (j + 0.5) + fm.getAscent() / 2));
      }
    }
  }
}
