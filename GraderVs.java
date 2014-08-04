class GraderVs implements Grader {
  private Learner<T3State,T3Move> _opp;
  private int _games;
  public GraderVs(Learner<T3State,T3Move> opponent, int games) {
    _opp = opponent;
    _games = games;
  }
  public double grade(Genome g) {
    NetLearner student = NetLearner.fromGenome(g);
    double score = 0;
    for (int i = 0; i < _games; i++) {
      Game game = new Game(student, _opp);
      game.play();
      switch(game.winner()) {
        case -1: // tie
          score += 1.0;
          break;
        case 0: // student wins!
          score += 3.0;
          break;
        case 1: // student loses
          break;
      }
    }
    return score;
  }
}
