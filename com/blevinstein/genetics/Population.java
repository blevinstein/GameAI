package com.blevinstein.genetics;

import com.blevinstein.util.Util;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Population<T> {
  // NOTE: population size does not change
  protected List<T> population;
  // derived state
  private int epochs;

  public Population(Collection<T> collection) {
    population = new ArrayList<T>(collection);
    epochs = 0;
  }

  public Population(int size, Supplier<T> supplier) {
    population = new ArrayList<>();
    while (population.size() < size) {
      population.add(supplier.get());
    }
    epochs = 0;
  }

  public List<T> getPopulation() {
    return ImmutableList.copyOf(population);
  }

  public void evolve() {
    List<T> newPopulation = new ArrayList<T>();
    while (newPopulation.size() < population.size()) {
      T child;
      if (Math.random() < crossover_rate()) {
        // sexual mating
        child = crossover(sample(), sample());
      } else {
        // survival
        child = sample();
      }
      newPopulation.add(mutate(child));
    }
    population = newPopulation;
    epochs++;
  }

  public T sample() {
    double fitnessArray[] = new double[population.size()];
    for (int i = 0; i < population.size(); i++) {
      fitnessArray[i] = fitness(population.get(i));
    }
    int index = Util.choose(fitnessArray);
    return population.get(index);
  }

  public int age() { return epochs; }

  public double crossover_rate() { return 0.7; }
  public double preserve_top() { return 0.05; }

  public abstract double getFitness(T individual);
  public abstract T mutate(T individual);
  public abstract T crossover(T a, T b);
  
  // memoized fitness function
  private Map<T, Double> fitnessMemory = new HashMap<>();
  private double fitness(T individual) {
    if (fitnessMemory.containsKey(individual)) {
      return fitnessMemory.get(individual);
    } else {
      double value = getFitness(individual);
      fitnessMemory.put(individual, value);
      return value;
    }
  }

  // returns the top n individuals by fitness
  public List<T> bestN(int n) {
    // add all items to a priority queue to sort by fitness
    PriorityQueue<T> pq = new PriorityQueue<T>(population.size(),
        (i1, i2) -> Double.compare(fitness(i2), fitness(i1)));
    for (T individual : population) {
      pq.add(individual);
    }

    // pop the indices of the top N items off the priority queue
    ArrayList<T> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      T individual = pq.poll();
      list.add(individual);
    }
    return list;
  }
  
  // return various information about the population
  public String stats() {
    T best = null;
    T worst = null;
    double avgFitness = 0;
    for (T individual : population) {
      if (best == null || fitness(individual) > fitness(best)) { best = individual; }
      if (worst == null || fitness(individual) < fitness(worst)) { worst = individual; }
      avgFitness += fitness(individual);
    }
    avgFitness /= population.size();
    return String.format("Best %.0f Worst %.0f Avg %.2f",
        fitness(best), fitness(worst), avgFitness);
  }
}
