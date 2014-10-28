package com.blevinstein.net;

import com.blevinstein.genetics.Population;
import com.blevinstein.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
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
  
  private final double MAX_MUTATION = 1.0;
  private final double MUTATION_RATE = 0.1;
  private final double RESIZE_RATE = 0.01;

  @Override
  public NeuralNet mutate(NeuralNet self) {
    // TODO: fix this code to allow architecture to mutate w/o ugliness
    // TODO: allow number of layers to change?
    int N = self.weights().length;
    RealMatrix w[] = new RealMatrix[N];
    for (int k = 0; k < N; k++) {
      // HACKy
      int rows = k == 0 ?                        // if first matrix
                 self.weights()[0].getRowDimension() : // number of inputs
                 w[k - 1].getColumnDimension();  // else copy from last layer
      int cols = self.weights()[k].getColumnDimension();
      // with some probability, change the output size of the matrix
      if (k < N - 1 && Math.random() < RESIZE_RATE) {
        cols += Math.random() < 0.5 ? 1 : -1;
        if (cols < 2) { cols = 2; } // must have 2 neurons, 1 + bias, in each layer
      }

      w[k] = MatrixUtils.createRealMatrix(rows, cols);
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          // HACK: looks ugly
          double value = i < self.weights()[k].getRowDimension() &&
                         j < self.weights()[k].getColumnDimension() ?
                         self.weights()[k].getEntry(i, j) :
                         Math.random() < 0.5 ? 1 : -1;
          // with probability MUTATION_RATE..
          if (Math.random() < MUTATION_RATE) {
            // ..alter the weight by less than MAX_MUTATION
            value += Util.random() * MAX_MUTATION;
          }
          w[k].setEntry(i, j, value);
        }
      }
    }
    return new NeuralNet(w);
  }

  @Override
  public NeuralNet crossover(NeuralNet self, NeuralNet other) {
    // TODO: handle crossover between varied architectures?
    // TODO: don't just cut between matrices
    RealMatrix otherWeights[] = other.weights();

    if (self.weights().length != otherWeights.length) {
      throw new RuntimeException("Invalid crossover attempted.");
    }

    int N = self.weights().length;
    RealMatrix newWeights[] = new RealMatrix[N];
    // choose a point to cut
    int cross = (int)(Math.random() * (N + 1));
    // return this[0..x] :: other[x..N]
    for (int i = 0; i < N; i++) {
      if (i < cross) {
        newWeights[i] = self.weights()[i].copy();
      } else {
        newWeights[i] = otherWeights[i].copy();
      }
    }
    return new NeuralNet(newWeights);
  }
}
