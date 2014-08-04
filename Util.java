public class Util {
  // choose from an array, where the probability of choosing any index
  // is proportional to prob[index]
  // NOTE: expects positive values in the array
  public static int choose(double prob[]) {
    double total = 0;
    for (int i = 0; i < prob.length; i++) total += prob[i];
    
    double chosen = total * Math.random();
    int k = 0;
    while (chosen > prob[k]) chosen -= prob[k++];
    return k;
  }

  // returns a random number between -1 and 1
  public static double random() {
    return Math.random() * 2 - 1;
  }
}
