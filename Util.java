import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

// Various static utility functions.

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

  public static final int CENTER = 0, NE = 1, S = 2;
  public static void placeText(Graphics g, int align, String s, int x, int y) {
    FontMetrics fm = g.getFontMetrics();
    switch(align) {
      case CENTER:
        g.drawString(s, x - fm.stringWidth(s)/2, y + fm.getAscent()/2);
        break;
      case NE:
        g.drawString(s, x - fm.stringWidth(s), y + fm.getAscent());
        break;
      case S:
        g.drawString(s, x - fm.stringWidth(s)/2, y);
        break;
      // TODO: add more cases
    }
  }

  public static boolean[] dtob(double values[]) {
    boolean b[] = new boolean[values.length];
    for (int i = 0; i < values.length; i++)
      b[i] = values[i] > 0;
    return b;
  }
  public static double[] btod(boolean bits[]) {
    double d[] = new double[bits.length];
    for (int i = 0; i < bits.length; i++)
      d[i] = bits[i] ? 1.0 : -1.0;
    return d;
  }

  public static void drawHistogram(Graphics g, double values[], double bucketSize,
                                   int x, int y, int sx, int sy) {
    // determine min and max values
    double min = Arrays.stream(values).min().orElse(0.0);
    double max = Arrays.stream(values).max().orElse(1.0);

    int buckets = (int)Math.floor((max-min)/bucketSize+1); // number of buckets needed
    int width = sx / buckets;

    // put each value into a bucket
    int count[] = new int[buckets];
    int most = 0;
    for (int i = 0; i < values.length; i++) {
      int b = (int)((values[i]-min)/bucketSize);
      count[b]++;
      if (count[b] > count[most]) most = b;
    }

    // draw the buckets
    for (int i = 0; i < count.length; i++) {
      int height = sy * count[i]/count[most];
      g.fillRect(x + width * i, y + sy - height,
                 width,         height);
      // add label
      /*
      Util.placeText(g, Util.S,
                     String.format("%.1f - %.1f", min + width * i, min + width * (i+1)),
                     (int)(x + width * (i + 0.5)), y + sy);
      */
    }
  }
}
