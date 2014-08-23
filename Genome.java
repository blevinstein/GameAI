// An interface used for a Population, which allows some underlying object to
// simulate random mutation and sexual reproduction.
//
// should be implemented like this:
// class Individual implements Genome<Individual>
// for ugly typing reasons

interface Genome<Self extends Genome<Self>> {
  // return a slightly perturbed version
  public Self mutate();

  // return the sexual offspring of two genomes
  public Self crossover(Self other);
}
