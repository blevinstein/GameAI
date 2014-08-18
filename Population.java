import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.PriorityQueue;

class Population<T extends Genome<T>> {
  // TODO: track best individual, best fitness, etc
  private final double CROSSOVER_RATE = 0.7;
  private final double ELITE = 0.05;

  // TODO: could be confused for pop()/push()... should make clearer
  private ArrayList<T> _pop;
  public ArrayList<T> pop() { return _pop; }

  private double[] _fitness; // private for HIPPA reasons
  
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

    /*
    // print various information about the population
    // TODO: generalize and move somewhere else
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
    */
    
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
}
