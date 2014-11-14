package com.blevinstein.net;

import static com.blevinstein.net.Util.chain;

import com.blevinstein.genetics.Population;
import com.blevinstein.util.Util;

import java.util.ArrayList; import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

// Each generation gets a copy of the top ELITE portion of the previous.
// Other individuals are generated based on CROSSOVER_RATE.
//   With probability CROSSOVER_RATE, a new individual is sexually produced by
//   sampling two individuals from the _fitness-weighted population.
//   With probability 1-CROSSOVER_RATE, a new individual is copied from the old,
//   sampling from the _fitness-weighted population.

public abstract class NetPopulation extends Population<NeuralNet> {
  public NetPopulation(Collection<NeuralNet> collection) {
    super(collection);
  }

  public NetPopulation(int size, Supplier<NeuralNet> supplier) {
    super(size, supplier);
  }

  public abstract double getFitness(NeuralNet net);
  
  @Override
  public NeuralNet mutate(NeuralNet net) {
    double maxMutation = 1.0;
    double mutationRate = 0.1;
    double resizeRate = 0.01;
    // TODO: allow number of layers to change?

    // Get current dimensions
    List<Integer> dims = new ArrayList<>();
    dims.add(net.getLayer(0).getRowDimension());
    for (int i = 0; i < net.size(); i++) {
      dims.add(net.getLayer(i).getColumnDimension());
    }
    // Tweak dimensions, ignoring first and last
    for(int i = 1; i < dims.size() - 1; i++) {
      if (Math.random() < resizeRate) {
        int newDim = dims.get(i) + Math.random() < 0.5 ? 1 : -1;
        if (newDim < 2) { newDim = 2; }
        dims.set(i, newDim);
      }
    }

    // Create new layers
    List<RealMatrix> newLayers = new ArrayList<>();
    int oldLayerIndex = 0;
    for (Pair<Integer, Integer> dim : chain(dims)) {
      int rows = dim.getLeft(), cols = dim.getRight();
      RealMatrix newLayer = MatrixUtils.createRealMatrix(rows, cols);
      RealMatrix oldLayer = net.getLayer(oldLayerIndex++);
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          double value;
          if (i < oldLayer.getRowDimension() && j < oldLayer.getColumnDimension()) {
            value = oldLayer.getEntry(i, j);
          } else {
            value = NeuralNet.newEntry();
          }
          if (Math.random() < mutationRate) {
            value += Util.random() * maxMutation;
          }
          newLayer.setEntry(i, j, value);
        }
      }
      newLayers.add(newLayer);
    }
    return new NeuralNet(newLayers);
  }

  @Override
  public NeuralNet crossover(NeuralNet a, NeuralNet b) {
    // TODO: handle crossover between varied architectures?
    // TODO: don't just cut between matrices
    if (a.size() != b.size()) {
      throw new RuntimeException("Invalid crossover attempted.");
    }

    // Choose layers randomly between a and b
    List<RealMatrix> newLayers = new ArrayList<>();
    for (int i = 0; i < a.size(); i++) {
      if (Math.random() < 0.5) {
        newLayers.add(a.getLayer(i));
      } else {
        newLayers.add(b.getLayer(i));
      }
    }

    return new NeuralNet(newLayers);
  }
}
