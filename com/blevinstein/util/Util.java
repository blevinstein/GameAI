package com.blevinstein.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Area;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

// Various static utility functions.

public abstract class Util {
  // choose from an array, where the probability of choosing any index
  // is proportional to prob[index]
  // NOTE: expects positive values in the array
  public static int choose(double prob[]) {
    double total = 0;
    for (int i = 0; i < prob.length; i++) {
      if (prob[i] < 0) {
        throw new IllegalArgumentException("choose() cannot handle negative elements");
      }
      total += prob[i];
    }

    double chosen = total * Math.random();
    int k = 0;
    while (chosen > prob[k]) { chosen -= prob[k++]; }
    return k;
  }

  // returns a random number between -1 and 1
  public static double random() {
    return Math.random() * 2 - 1;
  }

  public static final int CENTER = 0, NE = 1, S = 2, SE = 3, NW = 4;
  public static void placeText(Graphics g, int align, String s, int x, int y) {
    FontMetrics fm = g.getFontMetrics();
    switch (align) {
      case CENTER:
        g.drawString(s,
                     x - fm.stringWidth(s) / 2,
                     y + fm.getAscent() / 2 - fm.getDescent() / 2);
        break;
      case NE:
        g.drawString(s,
                     x - fm.stringWidth(s),
                     y + fm.getAscent());
        break;
      case NW:
        g.drawString(s, x, y + fm.getAscent());
        break;
      case S:
        g.drawString(s, x - fm.stringWidth(s) / 2, y - fm.getDescent());
        break;
      case SE:
        g.drawString(s, x - fm.stringWidth(s), y - fm.getDescent());
        break;
      default:
        throw new UnsupportedOperationException("Not implemented!");
    }
  }

  public static void drawHistogram(Graphics g, double values[], double bucketSize,
                                   int x, int y, int sx, int sy) {
    // determine min and max values
    double min = Arrays.stream(values).min().orElse(0.0);
    double max = Arrays.stream(values).max().orElse(1.0);

    int buckets = (int)Math.floor((max - min) / bucketSize + 1); // number of buckets needed
    int width = sx / buckets;

    // put each value into a bucket
    int count[] = new int[buckets];
    int most = 0;
    for (int i = 0; i < values.length; i++) {
      int b = (int)((values[i] - min) / bucketSize);
      count[b]++;
      if (count[b] > count[most]) { most = b; }
    }

    // draw the buckets
    for (int i = 0; i < count.length; i++) {
      int height = sy * count[i] / count[most];
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(x + width * i, y + sy - height,
                 width,         height);

      // add label
      g.setColor(Color.BLACK);
      Util.placeText(g, Util.S,
                     String.format("[%.1f, %.1f)", min + bucketSize * i, min + bucketSize * (i + 1)),
                     (int)(x + width * (i + 0.5)), y + sy - 10);
    }
  }

  public static Boolean[] randomBits(int n) {
    Boolean b[] = new Boolean[n];
    for (int i = 0; i < n; i++) {
      b[i] = Math.random() < 0.5;
    }
    return b;
  }

  // gives whether two circles, centered at point p with radius r, intersect
  public static boolean intersects(Vector2D p1, double r1, Vector2D p2, double r2) {
    return p1.distance(p2) < (r1 + r2);
  }

  // gives whether two Shapes intersect
  public static boolean intersects(Shape s1, Shape s2) {
    Area area = new Area(s1);
    area.intersect(new Area(s2));
    return !area.isEmpty();
  }

  // elementwise multiplication of Vector2D's
  public static Vector2D multiply(Vector2D a, Vector2D b) {
    return new Vector2D(a.getX() * b.getX(), a.getY() * b.getY());
  }

  // outline and fill a shape
  public static void drawAndFill(Graphics g, Shape s, Color draw, Color fill) {
    Graphics2D g2 = (Graphics2D)g;
    g2.setColor(fill);
    g2.fill(s);
    g2.setColor(draw);
    g2.draw(s);
  }
}
