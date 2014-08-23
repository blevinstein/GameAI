import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.PriorityQueue;

// Represents a population of individuals, to be bred and evolved according to
//   evolutionary algorithm.
//
// Each generation gets a copy of the top ELITE portion of the previous.
// Other individuals are generated based on CROSSOVER_RATE.
//   With probability CROSSOVER_RATE, a new individual is sexually produced by
//   sampling two individuals from the _fitness-weighted population.
//   With probability 1-CROSSOVER_RATE, a new individual is copied from the old,
//   sampling from the _fitness-weighted population.
//
// New generations are the same size as old, unless explicitly indicated.
//
// epoch()
// epoch(new_size)

class Population<T extends Genome<T>> {
  private final double CROSSOVER_RATE = 0.7;
  private final double ELITE = 0.05;

  // TODO: could be confused for pop()/push()... should make clearer
  private ArrayList<T> _pop;
  public ArrayList<T> pop() { return _pop; }

  private double[] _fitness; // private for HIPPA reasons
  public double[] fitness() { return _fitness; }
  
  private Grader<T> _grader;
  public void setGrader(Grader<T> grader) { _grader = grader; }

  // NOTE: all constructors use DefaultGrader.getDefaultGrader() to avoid
  //       problems with serialization
  public Population(int size, Supplier<T> generator) {
    // HACK: create a dummy object to get it's class
    _grader = DefaultGrader.getDefaultGrader(generator.get().getClass().getName());
    _pop = new ArrayList<T>();
    while (_pop.size() < size) _pop.add(generator.get());
    _fitness = calcFitness();
  }
  public Population(List<T> pop) {
    _grader = DefaultGrader.getDefaultGrader(pop.get(0).getClass().getName());
    _pop = new ArrayList<T>(pop);
    _fitness = calcFitness();
  }
  public Population(T array[]) {
    _grader = DefaultGrader.getDefaultGrader(array[0].getClass().getName());
    _pop = new ArrayList<T>();
    for (T genome : array) _pop.add(genome);
    _fitness = calcFitness();
  }

  // simulate an epoch, returning a more evolved population
  public Population<T> epoch() { return epoch(_pop.size()); }
  public Population<T> epoch(int size) {
    ArrayList<T> newList = new ArrayList<T>(bestN((int)(ELITE * size)));
    while (newList.size() < size) {
      T child;
      if (Math.random() < CROSSOVER_RATE) {
        // sexual mating
        child = sample().crossover(sample());
      } else {
        // survival
        child = sample();
      }
      newList.add(child.mutate());
    }
    Population<T> newPop = new Population<T>(newList);
    newPop.setGrader(_grader);
    return newPop;
  }

  // returns an array of fitness values corresponding to pop members
  private double[] calcFitness() {
    // create a thread pool
    ExecutorService threadPool = Executors.newCachedThreadPool();

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

    return f;
  }

  // return various information about the population
  public String stats() {
    double best = 0;
    double worst = 0;
    double avg = 0;
    for (int i = 0; i < _fitness.length; i++) {
      if (_fitness[i] > best || i == 0) best = _fitness[i];
      if (_fitness[i] < worst || i == 0) worst = _fitness[i];
      avg += _fitness[i];
    }
    avg /= _fitness.length;
    return String.format("Best %.0f Worst %.0f Avg %.2f", best, worst, avg);
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
}
