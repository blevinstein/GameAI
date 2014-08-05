class Genome {
  private double[] _genes;
  public double[] genes() { return _genes; }
  public int size() { return _genes.length; }

  // creates genome with random values in [-1..1]
  public Genome(int size) {
    _genes = new double[size];
    for (int i = 0; i < size; i++) _genes[i] = Util.random();
  }
  // double[] -> Genome
  public Genome(double[] genes) {
    _genes = genes;
  }

  // return a slightly perturbed version
  public Genome mutate(double mutationRate, double maxMutation) {
    double newGenes[] = new double[_genes.length];
    for (int i = 0; i < _genes.length; i++) {
      newGenes[i] = _genes[i];
      // with probability mutationRate..
      if (Math.random() < mutationRate) {
        // alter the gene by less than maxMutation
        newGenes[i] += Util.random() * maxMutation;
      }
    }
    return new Genome(newGenes);
  }

  // return this[0..x] :: other[x..N] for random x
  public Genome crossover(Genome other) {
    assert _genes.length == other.genes().length;

    int len = _genes.length;

    // choose a point to cut
    int cross = (int)(Math.random() * len);

    // copy a piece from each parent
    double newGenes[] = new double[len];
    System.arraycopy(_genes, 0, newGenes, 0, cross);
    System.arraycopy(other.genes(), cross, newGenes, cross, len - cross);
    return new Genome(newGenes);
  }
}
