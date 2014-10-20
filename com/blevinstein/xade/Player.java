package com.blevinstein.xade;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Player {
  private List<Army> armies;
  private Function<World, Map<Army, State>> strategy;

  public Player(Function<World, Map<Army, State>> strategy) {
    this.strategy = strategy;
  }

  public Map<Army, State> getMoves(World world) {
    return strategy.apply(world);
  }

  public List<Army> getArmies() { return armies; }
}
