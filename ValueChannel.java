import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

// Converts an image into neuron inputs by slicing the image into a N x M grid
// and taking the average value (as in HSV) of each section of the image.
//
// TODO: MultiChannel, construct w/ Channel[], e.g. RGBChannel
// TODO: GridChannel, abstract out grid logic

public class ValueChannel extends Channel {
  private int _n, _m;

  public ValueChannel(int n, int m) {
    _n = n;
    _m = m;
  }

  public double[] toDoubles(BufferedImage image) {
    int w = image.getWidth(), h = image.getHeight();

    double values[] = new double[_n*_m];
    for (int i = 0; i < _n; i++) {
      for (int j = 0; j < _m; j++) {
        int neuron = i * _m + j;
        // for each rectangle in an N x M grid over W x H pixels
        BufferedImage slice = image.getSubimage(w*i/_n, h*j/_m, w/_n, h/_m);

        double avg = avgValue(slice);

        // set the input to each neuron
        values[neuron] = avg * 2.0 - 1.0; // scale to [-1, 1]
      }
    }
    return values;
  }

  // Gets the average value (as in HSV) of all pixels in an image
  private double avgValue(BufferedImage image) {
    ColorModel cm = image.getColorModel();
    int w = image.getWidth(), h = image.getHeight();
  
    double total = 0.0;
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        // NOTE: in HSV, V = max(R,G,B)
        // convert from 0 - 255 to 0.0 - 1.0
        int index = x * h + y;
        total += Math.max(Math.max(cm.getRed(index),
                                   cm.getGreen(index)),
                                   cm.getBlue(index)) / 255.0;
      }
    }
    double avg = total / (w * h);
    return avg;
  }

  public int bits() {
    return _n * _m;
  }
}
