package com.blevinstein.xade;

import com.blevinstein.util.Util;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO(blevinstein): implement city radius growth?
// TODO(blevinstein): test add/remove/get for army counts

public class City implements Drawable {
  private Point location;
  private double radius = 10.0;
  private Map<Player, Integer> occupiers = new HashMap<>();
  // attributes
  private double buildRate = 1.0;
  private double killRate = 1.0;
  private double rotation;
  // derived state
  private Player owner = null;
  private double built = 0.0;
  private double angle = 0.0;

  public City(Point location, double radius) {
    this.location = location;
    this.radius = radius;
    this.rotation = Util.random();
  }
  
  public Point getLocation() { return location; }

  public double getRadius() { return radius; }

  public Player getOwner() { return owner; }

  // implements Drawable
  public void draw(Graphics2D g) {
    // draw outline
    g.setColor(owner == null ? Color.GRAY : owner.getColor());
    g.drawOval((int)(location.getX() - radius), (int)(location.getY() - radius), (int)(radius * 2), (int)(radius * 2));
  
    // Owner unit count displayed in middle
    if (owner != null) {
      g.setColor(owner.getColor());
      g.setFont(new Font("Arial", Font.PLAIN, (int)(radius/2)));
      Util.placeText(g, Util.CENTER, ""+get(owner),
          (int)location.getX(), (int)location.getY());
    }

    // Non-owner unit count displayed orbiting the city
    int nonOwners = occupiers.keySet().size() - (owner != null ? 1 : 0);
    if (nonOwners > 0) {
      List<Point> numberPoints = orbitPoints(nonOwners);
      int index = 0;
      for (Player player : occupiers.keySet()) {
        if (player == owner) { continue; }
        Point point = numberPoints.get(index++);
        g.setColor(player.getColor());
        g.setFont(new Font("Arial", Font.PLAIN, (int)(radius/2)));
        Util.placeText(g, Util.CENTER, ""+get(player), (int)point.getX(), (int)point.getY());
      }
    }
  }

  /*
   * Adds "count" armies controlled by player "p"
   */
  public void add(Player p, int count) {
    if (count < 0) { throw new IllegalArgumentException(); }
    if (count == 0) { return; }

    occupiers.put(p, get(p) + count);
  }

  /*
   * Removes "count" armies controlled by player "p"
   */
  public void remove(Player p, int count) {
    if (count < 0) { throw new IllegalArgumentException(); }
    if (count == 0) { return; }

    int currentCount = get(p);
    if (count > currentCount) { throw new IllegalStateException(); }

    int newCount = currentCount - count;
    if (newCount == 0) {
      occupiers.remove(p);
    } else {
      occupiers.put(p, newCount);
    }
  }

  /*
   * Gets the number of armies present controlled by player "p"
   */
  public int get(Player p) {
    return occupiers.containsKey(p) ? occupiers.get(p) : 0;
  }

  /*
   * Gets the total number of armies present
   */
  public int total() {
    int sum = 0;
    for (Player p : occupiers.keySet()) {
      sum += occupiers.get(p);
    }
    return sum;
  }

  public void update(double t) {
    // rotate
    angle += rotation * t;

    // if occupied by only one player, they become the owner
    Set<Player> players = occupiers.keySet();
    if (players.size() == 1) {
      owner = players.iterator().next();
    }
    // the owner builds troops
    if (owner == null) {
      built = 0.0;
    } else {
      built += buildRate * t;
      if (built >= 1.0) {
        add(owner, (int)built);
        built = built % 1.0;
      }
    }

    // combat
    Map<Player, Integer> killMap = new HashMap<>();
    for (Player player : players) {
      int strength = get(player);
      int all = total();
      double avgKilled = killRate * t * (all - strength) / all;
      int killed = (int)avgKilled + (Math.random() < avgKilled % 1.0 ? 1 : 0);
      if (killed > strength) { killed = strength; }
      killMap.put(player, killed);
    }
    for (Player player : killMap.keySet()) {
      remove(player, killMap.get(player));
    }
  }

  public List<Point> orbitPoints(int n) {
    if (n < 0) { throw new IllegalArgumentException(); }
    if (n == 0) { return ImmutableList.<Point>of(); }
    // calculate offset between points
    double offset = 2.0 * Math.PI / n;
    // calculate position of points
    List<Point> points = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      points.add(new Point(location.getX() + radius * Math.cos(offset * i + angle),
          location.getY() + radius * Math.sin(offset * i + angle)));
    }
    return points;
  }
}

