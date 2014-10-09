package com.blevinstein.image;

import com.blevinstein.net.Converter;
import com.blevinstein.net.NetAdapter;

import java.awt.image.BufferedImage;

// An extension of a NetAdapter, mapping from images to ints
public class ImageClassifier<T>
  extends NetAdapter<BufferedImage, T> {

  public ImageClassifier(Channel c, Converter<T> conv) {
    super(c, conv);
  }
}
