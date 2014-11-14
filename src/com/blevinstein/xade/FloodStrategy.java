package com.blevinstein.xade;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class FloodStrategy implements Strategy {
  private World world;
  private Player player;
  private int atom = 1;

  public FloodStrategy(World world, Player player) {
    this.world = world;
    this.player = player;
  }

  private double strength(City city) {
    return Math.pow(city.get(player) + 1.0, 2) / (city.total() + 1.0);
  }

  @Override
  public List<Move> getMoves() {
    // find my strongest city and weakest target
    City strongest = null;
    City weakest = null;
    for (City city : world.getCityList()) {
      if (strongest == null
          || strength(city) > strength(strongest)) {
        strongest = city;
      }
      if (weakest == null
          || city.total() < weakest.total()) {
        if (city.getOwner() == player) { continue; }
        weakest = city;
      }
    }
    if (strongest == null || weakest == null) {
      return ImmutableList.of();
    }
    int armies = (strongest.get(player) - weakest.get(player)) / 2;
    if (armies < atom) {
      return ImmutableList.of();
    }
    atom++;
    return ImmutableList.of(new Move(armies, strongest, weakest));
  }
}
