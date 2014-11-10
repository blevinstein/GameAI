package com.blevinstein.image;

import com.blevinstein.net.Converter;
import com.blevinstein.net.Signal;

import java.awt.image.BufferedImage;

// Represents a mapping between a BufferedImage and neuron voltages.
//
// Used as the input to an ImageClassifier. Only converts one way, so it's
// invalid to use it as an output converter.

public abstract class Channel implements Converter<BufferedImage> {
  public abstract Signal toSignal(BufferedImage image);
  public abstract int bits();

  // Input-only by default
  // NOTE: to return a low-res approximation, a Channel implementation can
  //       override this method
  public BufferedImage fromSignal(Signal signal) {
    throw new UnsupportedOperationException();
  }
}
