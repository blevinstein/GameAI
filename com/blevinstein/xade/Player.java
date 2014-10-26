package com.blevinstein.xade;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Player {
  private List<Army> armies;
  private Strategy strategy;

  public Player(Strategy strategy) {
    this.strategy = strategy;
  }

  public List<Move> getMoves(World world) {
    return strategy.getMoves(world);
  }

  public List<Army> getArmies() { return armies; }
}
