package com.blevinstein.xade;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Player {
  private List<Army> armies;
  private Function<World, List<Move>> strategy;

  public Player(Function<World, List<Move>> strategy) {
    this.strategy = strategy;
  }

  public List<Move> getMoves(World world) {
    return strategy.apply(world);
  }

  public List<Army> getArmies() { return armies; }
}
