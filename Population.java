import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Population {
  // TODO: track best individual, best fitness, etc
  private final double MAX_MUTATION = 1.0;
  private final double MUTATION_RATE = 0.1;
  private final double CROSSOVER_RATE = 0.7;
  private final double ELITE = 0.1;

  private ArrayList<Genome> _pop;
  public ArrayList<Genome> pop() { return _pop; }

  private Grader _grader;
  private double[] _fitness; // private for HIPPA reasons

  public Population(Grader grader, int size, int genomeLen) {
    _grader = grader;
    _pop = new ArrayList<Genome>();
    while (_pop.size() < size) _pop.add(new Genome(genomeLen));
    _fitness = calcFitness();
  }
  public Population(Grader grader, ArrayList<Genome> pop) {
    _grader = grader;
    _pop = pop;
    _fitness = calcFitness();
  }
  public Population(Grader grader, double array[][]) {
    _grader = grader;
    _pop = new ArrayList<Genome>();
    for (double genome[] : array) _pop.add(new Genome(genome));
    _fitness = calcFitness();
  }

  // simulate an epoch, resulting in a more evolved population
  public Population epoch() { return epoch(_pop.size()); }
  public Population epoch(int size) {
    ArrayList<Genome> newPop = new ArrayList<Genome>();
    //ArrayList<Genome> newPop = new ArrayList<Genome>(bestN((int)(ELITE * size)));
    for (int i = 0; i < size; i++) {
      Genome child;
      if (Math.random() < CROSSOVER_RATE) {
        // sexual mating
        child = sample().crossover(sample());
      } else {
        // survival
        child = sample();
      }
      newPop.add(child.mutate(MUTATION_RATE, MAX_MUTATION));
    }
    return new Population(_grader, newPop);
  }

  // returns an array of fitness values corresponding to pop members
  private double[] calcFitness() {
    // TODO: parallelize this
    System.out.print("Grading new population... ");

    long before = System.currentTimeMillis();

    double f[] = new double[_pop.size()];
    double best = 0;
    double worst = 0;
    double avg = 0;
    for (int i = 0; i < f.length; i++) {
      f[i] = _grader.grade(_pop.get(i));
      if (f[i] > best || i == 0) best = f[i];
      if (f[i] < worst || i == 0) worst = f[i];
      avg += f[i];
      f[i] *= f[i];
    }
    avg /= f.length;
    System.out.print("Best " + best + " Worst " + worst + " Avg " + avg);

    long duration = System.currentTimeMillis() - before;
    System.out.println(" " + duration + "ms");
    return f;
  }

  public List<Genome> bestN(int n) {
    ArrayList<Genome> list = new ArrayList<Genome>(_pop);
    Collections.sort(list, (g1, g2) -> Double.compare(_grader.grade(g1),(_grader.grade(g2))));
    return list.subList(0, n);
  }

  // choose a successor, weighted on fitness
  public Genome sample() {
    int index = Util.choose(_fitness);
    return _pop.get(index);
  }

  // dump to Doubles, to allow saving as JSON
  public double[][] toDoubles() {
    double array[][] = new double[_pop.size()][];
    for (int i = 0; i < _pop.size(); i++) {
      array[i] = _pop.get(i).genes();
    }
    return array;
  }
}
