import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.PriorityQueue;

class Population<T extends Genome<T>> {
  // TODO: track best individual, best fitness, etc
  private final double CROSSOVER_RATE = 0.7;
  private final double ELITE = 0.05;

  private ArrayList<T> _pop;
  public ArrayList<T> pop() { return _pop; }

  private Grader<T> _grader;
  private double[] _fitness; // private for HIPPA reasons

  // TODO: add default grader for a type? for any type? add setGrader() method?

  public Population(Grader<T> grader, int size, Callable<T> generator) {
    _grader = grader;
    _pop = new ArrayList<T>();
    try { // HACK: allow generic exceptions from Callable
      while (_pop.size() < size) _pop.add(generator.call());
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    _fitness = calcFitness();
  }
  public Population(Grader<T> grader, Collection<T> pop) {
    _grader = grader;
    _pop = new ArrayList<T>(pop);
    _fitness = calcFitness();
  }
  public Population(Grader<T> grader, T array[]) {
    _grader = grader;
    _pop = new ArrayList<T>();
    for (T genome : array) _pop.add(genome);
    _fitness = calcFitness();
  }

  // simulate an epoch, returning a more evolved population
  public Population<T> epoch() { return epoch(_pop.size()); }
  public Population<T> epoch(int size) {
    ArrayList<T> newPop = bestN((int)(ELITE * size));
    while (newPop.size() < size) {
      T child;
      if (Math.random() < CROSSOVER_RATE) {
        // sexual mating
        child = sample().crossover(sample());
      } else {
        // survival
        child = sample();
      }
      newPop.add(child.mutate());
    }
    return new Population<T>(_grader, newPop);
  }

  // returns an array of fitness values corresponding to pop members
  private double[] calcFitness() {
    // create a thread pool
    ExecutorService threadPool = Executors.newCachedThreadPool();

    long before = System.currentTimeMillis();

    // calculate fitness values in individual threads
    double f[] = new double[_pop.size()];
    for (int i = 0; i < f.length; i++) {
      final int j = i;
      threadPool.submit(() -> {
        f[j] = _grader.grade(_pop.get(j));
      });
    }

    // wait until thread pool is done
    threadPool.shutdown();
    while (true) {
      try {
        if (threadPool.awaitTermination(5, TimeUnit.MILLISECONDS))
          break;
      } catch(InterruptedException e) {}
    }

    double best = 0;
    double worst = 0;
    double avg = 0;
    for (int i = 0; i < f.length; i++) {
      if (f[i] > best || i == 0) best = f[i];
      if (f[i] < worst || i == 0) worst = f[i];
      avg += f[i];
    }
    avg /= f.length;
    
    System.out.print("Best " + best + " Worst " + worst + " Avg " + avg);
    long duration = System.currentTimeMillis() - before;
    System.out.println(" " + duration + "ms");
    return f;
  }

  public List<T> bestN(int n) {
    // add all elements to a priority queue
    PriorityQueue<Integer> pq = new PriorityQueue<Integer>(_pop.size(), (i1, i2) -> Double.compare(_fitness[i2],_fitness[i1]));
    for (int i = 0; i < _pop.size(); i++) {
      pq.add(i);
    }

    // pop the top N items off the priority queue
    ArrayList<T> list = new ArrayList<T>();
    for (int i = 0; i < n; i++) {
      int index = pq.poll();
      list.add(_pop.get(index));
    }
    return list;
  }

  // choose a successor, weighted on fitness
  public T sample() {
    int index = Util.choose(_fitness);
    return _pop.get(index);
  }

  /*
  // dump to Doubles, to allow saving as JSON
  // HACK: return type double[][][][] is NeuralNet-specific
  public double[][][][] toDoubles() {
    double array[][][][] = new double[_pop.size()][][][];
    for (int i = 0; i < _pop.size(); i++) {
      // HACK: cast to NeuralNet specifically
      NeuralNet n = (NeuralNet)_pop.get(i);
      array[i] = n.toDoubles();
    }
    return array;
  }
  */
}
