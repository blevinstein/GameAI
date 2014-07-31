public class Util {
  public static int choose(double prob[]) {
    double total = 0;
    for (int i = 0; i < prob.length; i++) total += prob[i];
    
    double chosen = total * Math.random();
    int k = 0;
    while (chosen > prob[k]) chosen -= prob[k++];
    return k;
  }
}
