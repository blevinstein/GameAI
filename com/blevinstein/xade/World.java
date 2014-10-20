package com.blevinstein.xade;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {
  private List<City> cities;
  private List<Player> players;

  public void step(double timeStep) {
    // get moves from each player
    Map<Player, Map<Army, State>> moveLists = new HashMap<>();
    for (Player player : players) {
      Map<Army, State> moves = player.getMoves(this);
      moveLists.put(player, player.getMoves(this));
    }
    // update each army
    for (Player player : players) {
      Map<Army, State> moves = moveLists.get(player);
      for (Army army : player.getArmies()) {
        if (moves.containsKey(army)) {
          army.setState(moves.get(army));
        } else {
          army.update(timeStep);
        }
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
