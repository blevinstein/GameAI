interface Genome<Self extends Genome<Self>> {
  // return a slightly perturbed version
  public Self mutate();

  // return the sexual offspring of two genomes
  public Self crossover(Self other);
}
