package com.blevinstein.xade;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {
  private List<City> cities;
  private List<Player> players;

  public World() {
    this.cities = new ArrayList<>();
    this.players = new ArrayList<>();
  }

  public void add(City city) {
    cities.add(city);
  }

  public void add(Player player) {
    players.add(player);
  }

  public void step(double timeStep) {
    // get moves from each player
    Map<Player, List<Move>> moveLists = new HashMap<>();
    for (Player player : players) {
      List<Move> moves = player.getMoves(this);
      moveLists.put(player, moves);
    }
    // update each city
    for (City city : cities) {
      city.update(timeStep);
    }
    // update each army
    for (Player player : players) {
      List<Move> moves = moveLists.get(player);
      for (Move move : moves) {
        // if move is possible
        // make move
      }
      for (Army army : player.getArmies()) {
        army.update(timeStep);
      }
    }
  }

  public void draw(Graphics2D g) {
    for (Player player : players) {
      for (Army army : player.getArmies()) {
        State state = army.getState();
        Drawable d = state.drawable();
        if (d != null) {
          d.draw(g);
        }
      }
    }
    for (City city : cities) {
      city.draw(g);
    }
  }
}
