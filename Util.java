import java.util.ArrayList;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class Util {
  // choose from an array, where the probability of choosing any index
  // is proportional to prob[index]
  // NOTE: expects positive values in the array
  public static int choose(double prob[]) {
    double total = 0;
    for (int i = 0; i < prob.length; i++) total += prob[i];
    
    double chosen = total * Math.random();
    int k = 0;
    while (chosen > prob[k]) chosen -= prob[k++];
    return k;
  }

  // returns a random number between -1 and 1
  public static double random() {
    return Math.random() * 2 - 1;
  }

  public static final int CENTER = 0, NE = 1;
  public static void placeText(Graphics g, int align, String s, int x, int y) {
    FontMetrics fm = g.getFontMetrics();
    switch(align) {
      case CENTER:
        g.drawString(s, x - fm.stringWidth(s)/2, y + fm.getAscent()/2);
        break;
      case NE:
        g.drawString(s, x - fm.stringWidth(s)/2, y + fm.getAscent()/2);
        break;
      // TODO: add more cases
    }
  }

  public static double randomBit() {
    return Math.random() < 0.5 ? 1.0 : -1.0;
  }
}
