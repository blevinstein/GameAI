package com.blevinstein.image;

import com.blevinstein.net.NeuralNet;
import com.blevinstein.net.Signal;
import com.google.common.base.Converter;

import java.awt.image.BufferedImage;

// An extension of a NetAdapter, mapping from images to ints
public class ImageClassifier {
  private NeuralNet net;
  private Converter<BufferedImage, Signal> inputConverter;
  private Converter<Signal, Integer> outputConverter;

  public ImageClassifier(NeuralNet net,
      Converter<BufferedImage, Signal> inputConverter,
      Converter<Signal, Integer> outputConverter) {
    this.net = net;
    this.inputConverter = inputConverter;
    this.outputConverter = outputConverter;
  }

  public NeuralNet.NetAdapter<BufferedImage,Integer> getAdapter() {
    return net.getAdapter(inputConverter, outputConverter);
  }
}