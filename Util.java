public class Util {
  /*
  public static int chooseOffset(double prob[]) {
    double newProb[] = new double[prob.length];
    for (int i = 0; i < prob.length; i++) newProb[i] = 0.5 * (prob[i] + 1);
    return choose(newProb);
  }
  */
  public static int choose(double prob[]) {
    double total = 0;
    for (int i = 0; i < prob.length; i++) total += prob[i];
    
    double chosen = total * Math.random();
    int k = 0;
    while (chosen > prob[k]) chosen -= prob[k++];
    return k;
  }
}
