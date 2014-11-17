package com.blevinstein.net;

import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import static com.blevinstein.util.Util.multiply;
import static com.blevinstein.util.Util.placeText;

public class Util {

  private static Logger logger = Logger.getLogger("com.blevinstein.net.Util");
  /**
   * chain({0, 1, 2, 3}) -> {(0, 1), (1, 2), (2, 3)}
   */
  public static <T> List<Pair<T, T>> chain(List<T> list) {
    List<Pair<T, T>> result = new ArrayList<>();
    for (int i = 0; i < list.size() - 1; i++) {
      result.add(Pair.of(list.get(i), list.get(i + 1)));
    }
    return result;
  }

  // Draws from [x,y] to [x+sx, y+sy].
  // Depicts neurons in layers, connected by synapses.
  // Neurons and synapses colored according to activation.
  // Synapse width corresponds to weight.
  // Arrowed "1.0 => 0.76" on neuron gives the input and output of the sigmoid.
  // mode determines method of drawing connections, MAG or SVD.
  // TODO: refactor this awful crap into another file

  public static enum Style {
    MAG, SVD
  }

  public static void drawState(Graphics g, NeuralNet net, Signal input,
                        int x, int y, int sx, int sy) {
    drawState(g, net, input, x, y, sx, sy, Style.MAG);
  }

  public static void drawState(Graphics g, NeuralNet net, Signal input,
                        int x, int y, int sx, int sy, Style mode) {
    // needed for drawing with Stroke's
    Graphics2D g2 = (Graphics2D) g;

    // perform propagation
    List<RealMatrix> layers = net.getLayers();
    List<Signal> wave = net.propagate(input);

    // make background gray
    g.setColor(new Color(0.5f, 0.5f, 0.5f));
    g.fillRect(x, y, sx, sy);

    // calculate spacing (grid)
    // calculate neuron size (diameter)
    int maxNeurons = layers.get(0).getRowDimension();
    for (RealMatrix layer : layers) {
      int neurons = layer.getColumnDimension();
      if (neurons > maxNeurons) {
        maxNeurons = neurons;
      }
    }
    Vector2D grid = new Vector2D(sx / wave.size(), sy / maxNeurons);
    // NOTE: 0.5 = arbitrary constant less than 1.0, for spacing
    double diameter = Math.min(grid.getX(), grid.getY()) * 0.5;

    // set font
    g.setFont(new Font("Arial", Font.PLAIN, (int) (diameter / 8)));

    // draw neurons
    for (int i = 0; i < wave.size(); i++) { // each layer
      for (int j = 0; j <= wave.get(i).size(); j++) { // each neuron
        // skip the last bias element
        if (i == wave.size() - 1 && j == wave.get(i).size()) {
          continue;
        }

        // calculate grayscale color to use
        float value = (float) (Signal.sigmoid(wave.get(i).get(j)) * 0.5 + 0.5);
        Color gray = new Color(value, value, value);
        Color tgray = new Color(value, value, value, 0.5f); // translucent gray
        Color contrast = value > 0.5 ? Color.BLACK : Color.WHITE;

        // draw synapses outgoing from layer i neuron j...
        if (i + 1 < wave.size()) { // except for last row

          // SVD-related preprocessing
          SingularValueDecomposition svd = new SingularValueDecomposition(
              layers.get(i));
          int p = svd.getRank();
          RealMatrix a[] = new RealMatrix[p];
          if (mode == Style.SVD) {
            RealMatrix u = svd.getU();
            RealMatrix v = svd.getVT();
            for (int k = 0; k < p; k++) {
              RealMatrix s = new DiagonalMatrix(p);
              s.setEntry(k, k, svd.getSingularValues()[k]);
              // IDEA: set to 1.0?
              //s.setEntry(k, k, 1.0);
              a[k] = u.multiply(s).multiply(v);
            }
          }

          // ...to neuron m
          for (int m = 0; m < wave.get(i + 1).size(); m++) {

            int widths[];
            Color colors[];

            double weight = layers.get(i).getEntry(j, m);

            // perform mode-specific processing
            switch (mode) {
              case MAG:
                if (Math.abs(weight) < 0.1) {
                  continue;
                } // skip synapses which aren't connected

                double mag = Math.abs(weight);

                widths = new int[1];
                // NOTE: 0.5 = arbitrary constant less than 1
                widths[0] = (int) (mag / (1 + mag) * diameter * 0.5);

                colors = new Color[]{tgray};

                break;
              case SVD:
                double[] rgb = new double[p < 3 ? p : 3];
                for (int k = 0; k < rgb.length; k++) {
                  rgb[k] = Math.abs(a[k].getEntry(j, m));
                }

                widths = new int[rgb.length];
                // NOTE: 0.5 = arbitrary constant less than 1
                for (int w = 0; w < widths.length; w++) {
                  widths[w] = (int) (rgb[w] / (1 + rgb[w]) * diameter * 0.5);
                }

                colors = new Color[]{new Color(1f, 0, 0, 0.5f),
                    new Color(0, 1f, 0, 0.5f),
                    new Color(0, 0, 1f, 0.5f)
                };

                break;
              default:
                throw new UnsupportedOperationException("Invalid mode.");
            }

            // for each connection
            for (int w = 0; w < widths.length; w++) {
              Vector2D origin = new Vector2D(x, y);
              Vector2D from = multiply(grid, new Vector2D(0.5 + i, 0.5 + j))
                  .add(origin);
              Vector2D to = multiply(grid, new Vector2D(1.5 + i, 0.5 + m))
                  .add(origin);
              // (x, y) perpendicular to (x, -y) or (-x, y)
              // both should be normalized
              Vector2D neuronVector = to.subtract(from);
              Vector2D perpVector =
                  new Vector2D(neuronVector.getY(), -neuronVector.getX());
              Vector2D mid = from.add(to).scalarMultiply(0.5);
              // NOTE: 0.2 is a constant close to 0.0
              Vector2D offset = perpVector
                  .scalarMultiply(0.2 * (w * 2.0 / (widths.length - 1) - 1.0))
                  .add(mid);
              g2.setColor(colors[w]);
              g2.setStroke(new BasicStroke(widths[w]));
              // g2.drawLine((int)from.getX(), (int)from.getY(),
              //             (int)to.getX(),   (int)to.getY());
              QuadCurve2D curve = new QuadCurve2D.Double();
              curve.setCurve(from.getX(), from.getY(),
                  offset.getX(), offset.getY(),
                  to.getX(), to.getY());
              g2.draw(curve);
              g2.setStroke(new BasicStroke(1.0f));

              // display the synapse weight
              g2.setColor(contrast);
              // t is used to determine placement along the synapse, to avoid
              //   labels overlapping. For simple midpoint text, set t=0.5.
              double t = (j + 1.0) / wave.get(i).size();
              placeText(g, com.blevinstein.util.Util.Align.CENTER, String.format("%.2f", weight),
                  (int) (x + grid.getX() * (i + 0.5 + t)),
                  (int) (y + grid.getY() * (0.5 + j + (m - j) * t)));
            }

          }
        }

        // diameter is halved for bias nodes
        double d = j == wave.get(i).size() ?
            diameter / 2 :
            diameter;
        // circle is centered on square [i,j] with given side length, diameter d
        g.setColor(gray);
        g.fillOval((int) (x + grid.getX() * (0.5 + i) - d / 2),
            (int) (y + grid.getY() * (0.5 + j) - d / 2),
            (int) d, (int) d);
        // draw border
        g.setColor(Color.BLACK);
        g.drawOval((int) (x + grid.getX() * (0.5 + i) - d / 2),
            (int) (y + grid.getY() * (0.5 + j) - d / 2),
            (int) d, (int) d);

        // display the neuron's pre- and post-sigmoid values
        // NOTE: biases don't get sigmoided
        g.setColor(contrast);
        String str = j == wave.get(i).size() ?
            String.format("%.2f", wave.get(i).get(j)) :
            String.format("%.2f => %.2f", wave.get(i).get(j),
                Signal.sigmoid(wave.get(i).get(j)));
        placeText(g, com.blevinstein.util.Util.Align.CENTER, str,
            (int) (x + grid.getX() * (0.5 + i)),
            (int) (y + grid.getY() * (0.5 + j)));
      }
    }
  }
}
