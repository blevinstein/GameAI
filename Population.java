import java.util.ArrayList;

class Population {
  // TODO: track best individual, best fitness, etc
  private final double MAX_MUTATION = 1.0;
  private final double MUTATION_RATE = 0.1;
  private final double CROSSOVER_RATE = 0.7;

  private ArrayList<Genome> _pop;
  public ArrayList<Genome> pop() { return _pop; }

  private Grader _grader;
  private double[] _fitness; // private for HIPPA reasons

  public Population(Population prev, int size) {
    _grader = prev._grader;
    _pop = new ArrayList<Genome>();
    for (int i = 0; i < size; i++) {
      Genome child;
      if (Math.random() < CROSSOVER_RATE) {
        // sexual mating
        child = prev.sample().crossover(prev.sample());
      } else {
        // survival
        child = prev.sample();
      }
      _pop.add(child.mutate(MUTATION_RATE, MAX_MUTATION));
    }
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

  // returns an array of fitness values corresponding to pop members
  private double[] calcFitness() {
    System.out.print("Grading new population..");
    double f[] = new double[_pop.size()];
    for (int i = 0; i < f.length; i++) {
      f[i] = _grader.grade(_pop.get(i));
      System.out.print(".");
    }
    System.out.println();
    return f;
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
