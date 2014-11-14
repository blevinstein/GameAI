package com.blevinstein.xade;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import org.junit.Test;

public class WorldTest {
  @Test
  public void testAddCity() {
    World w = new World();
    assertEquals(0, w.getCityCount());
    City a = new City(new Point(0.0, 0.0), 5.0);
    w.add(a);
    assertEquals(1, w.getCityCount());
    assertEquals(a, w.getCity(0));
  }

  @Test
  public void testAddPlayer() {
    World w = new World();
    assertEquals(0, w.getPlayerCount());
    Player a = new Player();
    w.add(a);
    assertEquals(1, w.getPlayerCount());
    assertEquals(a, w.getPlayer(0));
  }
}
