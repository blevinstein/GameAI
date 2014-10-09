package com.blevinstein.image;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

// Converts an image into neuron inputs by slicing the image into a N x M grid
// and taking the average value (as in HSV) of each section of the image.
//
// TODO: MultiChannel, construct w/ Channel[], e.g. RGBChannel

public class ValueChannel extends Channel {
  private int _n, _m;

  public ValueChannel(int n, int m) {
    _n = n;
    _m = m;
  }

  public double[] toDoubles(BufferedImage image) {
    if (image == null) {
      throw new IllegalArgumentException();
    }
    int w = image.getWidth(), h = image.getHeight();

    double values[] = new double[_n * _m];
    double max = 0.0;
    double min = 1.0;
    for (int i = 0; i < _n; i++) {
      for (int j = 0; j < _m; j++) {
        int neuron = i * _m + j;
        // for each rectangle in an N x M grid over W x H pixels
        BufferedImage slice = image.getSubimage(w * i / _n, h * j / _m, w / _n, h / _m);

        double value = avgValue(slice);
        if (value > max) { max = value; }
        if (value < min) { min = value; }

        // set the input to each neuron
        values[neuron] = value;
      }
    }
    // scale each value to [-1, 1)
    for (int i = 0; i < values.length; i++) {
      values[i] = (values[i] - min) / (max - min) * 2.0 - 1.0;
    }
    return values;
  }

  // Gets the average value (as in HSV) of all pixels in an image
  // NOTE: returns in range 0.0-1.0
  private double avgValue(BufferedImage image) {
    ColorModel cm = image.getColorModel();
    int w = image.getWidth(), h = image.getHeight();

    double total = 0.0;
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        // NOTE: in HSV, V = max(R,G,B)
        // convert from 0 - 255 to 0.0 - 1.0
        int color = image.getRGB(x, y);
        double value = Math.max(Math.max(cm.getRed(color),
                                         cm.getGreen(color)),
                                cm.getBlue(color)) / 255.0;
        total += value;
      }
    }
    double avg = total / (w * h);
    return avg;
  }

  public int bits() {
    return _n * _m;
  }
}
