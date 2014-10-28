package com.blevinstein.xade;

import com.google.common.collect.ImmutableList;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

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
  public List<City> getCityList() { return ImmutableList.copyOf(cities); }

  public void add(Player player) {
    players.add(player);
  }
  public int getPlayerCount() { return players.size(); }
  public Player getPlayer(int i) {
    if (i < 0 || i >= players.size()) { throw new IllegalArgumentException(); }
    return players.get(i);
  }
  public List<Player> getPlayerList() { return ImmutableList.copyOf(players); }

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
      List<Move> moves = player.getMoves();
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
        if (validMove(player, move)) {
          // Remove units from source city
          move.getSource().remove(player, move.getArmies());
          // Create a route
          Route route = SimpleRoute.shortest(move.getSource(), move.getDestination());
          // Create a new state
          State state = new OnRoute(route, move.getDestination(), 100.0);
          // Create an army with the new state
          Army newArmy = new Army(player, state, move.getArmies());
          player.add(newArmy);
        }
      }
      for (Iterator<Army> iter = player.getArmies().iterator(); iter.hasNext(); ) {
        Army army = iter.next();
        if (army.update(timeStep)) {
          iter.remove();
        }
      }
    }
  }

  public void draw(Graphics2D g) {
    // TODO: addDrawable(obj), perhaps also createPlayer/createArmy/createCity
    for (Player player : players) {
      for (Army army : player.getArmies()) {
        Drawable d = army.drawable();
        if (d != null) {
          d.draw(g);
        }
      }
    }
    for (City city : cities) {
      city.draw(g);
    }
  }

  // Returns nearest city to a given point, and the distance between the two
  // NOTE: distance is calculated as (distance_to_center - radius), so it is negative when the
  //     point is inside the radius of the city
  public Pair<City, Double> closestCity(Point point) {
    Pair<City, Double> closest = Pair.of(null, 0.0);
    for (City city : cities) {
      double distance = city.getLocation().sub(point).mag() - city.getRadius();
      if (closest.getLeft() == null || distance < closest.getRight()) {
        closest = Pair.of(city, distance);
      }
    }
    return closest;
  }
}
