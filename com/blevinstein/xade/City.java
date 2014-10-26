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

public class City implements Drawable {
  private Point location;
  private double radius = 10.0;
  private Map<Player, Integer> occupiers = new HashMap<>();
  // attributes
  private double buildRate = 0.1;
  // derived state
  private Player owner = null;
  private double buildTime = 0.0;

  public City(Point location, double radius) {
    this.location = location;
    this.radius = radius;
  }
  
  public Point getLocation() { return location; }

  public double getRadius() { return radius; }

  // implements Drawable
  public void draw(Graphics2D g) {
    // draw outline
    g.setColor(owner == null ? Color.GRAY : owner.getColor());
    g.drawOval((int)(location.getX() - radius), (int)(location.getY() - radius), (int)(radius * 2), (int)(radius * 2));
   
    if (owner != null) {
      g.setColor(owner.getColor());
      g.setFont(new Font("Arial", Font.PLAIN, 18));
      Util.placeText(g, Util.CENTER, occupiers.get(owner).toString(),
          (int)location.getX(), (int)location.getY());
    }
  }

  public void add(Player p, int count) {
    if (count < 0) { throw new IllegalArgumentException(); }
    if (count == 0) { return; }

    int currentCount = occupiers.containsKey(p) ? occupiers.get(p) : 0;
    occupiers.put(p, currentCount + count);
  }

  public void remove(Player p, int count) {
    if (count < 0) { throw new IllegalArgumentException(); }
    if (count == 0) { return; }

    int currentCount = occupiers.containsKey(p) ? occupiers.get(p) : 0;
    if (count > currentCount) { throw new IllegalStateException(); }

    int newCount = currentCount - count;
    if (newCount == 0) {
      occupiers.remove(p);
    } else {
      occupiers.put(p, newCount);
    }
  }

  public void update(double t) {
    // if occupied by only one player, they become the owner
    Set<Player> players = occupiers.keySet();
    if (players.size() == 1) {
      owner = players.iterator().next();
    }
    // the owner builds troops
    if (owner == null) {
      buildTime = 0.0;
    } else {
      buildTime += t;
      int produced = (int) (buildTime * buildRate);
      if (produced > 0) {
        add(owner, produced);
        buildTime -= produced / buildRate;
      }
    }
  }

  public List<Point> orbitPoints(int n) {
    if (n < 0) { throw new IllegalArgumentException(); }
    if (n == 0) { return ImmutableList.<Point>of(); }
    // calculate angle between points
    double angle = 2.0 * Math.PI / n;
    // calculate position of points
    List<Point> points = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      points.add(new Point(location.getX() + radius * Math.cos(angle * i),
          location.getY() + radius * Math.sin(angle * i)));
    }
    return points;
  }
}

