package com.blevinstein.xade;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class Player {
  // all cities occupied by player, not necessarily owned
  private List<City> cities = new ArrayList<>();
  private List<Army> armies = new ArrayList<>();
  private Strategy strategy;
  private Color color;

  public Player() {
    this.color = Color.BLACK;
    this.strategy = new NullStrategy();
  }

  public void add(Army army) {
    armies.add(army);
  }
  public void remove(Army army) {
    armies.remove(army);
  }

  public Player setColor(Color color) {
    this.color = color;
    return this;
  }

  public Player setStrategy(Strategy strategy) {
    this.strategy = strategy;
    return this;
  }

  public List<City> getCities() { return cities; }
  public List<City> getOwnedCities() {
    return cities.stream()
      .filter(city -> city.getOwner() == this)
      .collect(Collectors.toList());
  }

  public Color getColor() { return color; }

  public List<Move> getMoves() {
    return strategy.getMoves();
  }

  public List<Army> getArmies() { return armies; }
}
