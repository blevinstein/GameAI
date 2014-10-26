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
  public int getCityCount() { return cities.size(); }
  public City getCity(int i) {
    if (i < 0 || i >= cities.size()) { throw new IllegalArgumentException(); }
    return cities.get(i);
  }

  public void add(Player player) {
    players.add(player);
  }
  public int getPlayerCount() { return players.size(); }
  public Player getPlayer(int i) {
    if (i < 0 || i >= players.size()) { throw new IllegalArgumentException(); }
    return players.get(i);
  }

  public boolean validMove(Player player, Move move) {
    // Invalid arguments
    if (!cities.contains(move.getSource())
        || !cities.contains(move.getDestination())
        || move.getArmies() <= 0) {
      return false;
    }
    // Insufficient armies at source
    if (move.getSource().get(player) < move.getArmies()) {
      return false;
    }
    return true;
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
