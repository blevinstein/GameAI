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

  public World add(City city) {
    cities.add(city);
    return this;
  }

  public World add(Player player) {
    players.add(player);
    return this;
  }

  public void step(double timeStep) {
    // get moves from each player
    Map<Player, List<Move>> moveLists = new HashMap<>();
    for (Player player : players) {
      List<Move> moves = player.getMoves(this);
      moveLists.put(player, moves);
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

  public void draw(Graphics2D g) { draw(g, null); }
  public void draw(Graphics2D g, AffineTransform xfm) {
    for (Player player : players) {
      //Drawable.draw(g, player, xfm);
      for (Army army : player.getArmies()) {
        State state = army.getState();
        Drawable d = state.drawable();
        if (d != null) {
          Drawable.draw(g, d, xfm);
        }
      }
    }
    for (City city : cities) {
      Drawable.draw(g, city, xfm);
    }
  }
}
