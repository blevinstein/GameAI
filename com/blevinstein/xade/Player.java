package com.blevinstein.xade;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Player {
  private List<Army> armies;
  private Strategy strategy;
  private Color color;

  public Player(Color color, Strategy strategy) {
    this.color = color;
    this.strategy = strategy;
  }

  public Color getColor() { return color; }

  public List<Move> getMoves(World world) {
    return strategy.getMoves(world);
  }

  public List<Army> getArmies() { return armies; }
}
