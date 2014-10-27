package com.blevinstein.xade;

import com.blevinstein.util.Util;
import java.awt.Color;
import java.awt.Font;

public class Army {
  private Player player;
  private State state;
  private int size;

  public Army(Player player, State state, int size) {
    this.player = player;
    this.state = state;
    this.size = size;
  }

  public Player getPlayer() { return player; }

  public State getState() { return state; }

  public int getSize() { return size; }

  /*
   * Updates the state. Returns true if the army should be removed.
   */
  public boolean update(double t) {
    state = state.update(t);
    // intercept InCity state, remove Army
    if (state instanceof InCity) {
      InCity inCity = (InCity) state;
      inCity.getCity().add(player, size);
      state = null;
    }
    // null state causes Army to be removed
    if (state == null) {
      return true;
    }
    return false;
  }

  public Drawable drawable() {
    // draw army
    double dotRadius = 10.0;
    Point location = state.location();

    return (g) -> {
      if (location != null) {
        g.setColor(player.getColor());
        g.drawOval((int)(location.getX() - dotRadius), (int)(location.getY() - dotRadius),
            (int)(dotRadius * 2), (int)(dotRadius * 2));
        g.setFont(new Font("Arial", Font.PLAIN, (int)(dotRadius)));
        Util.placeText(g, Util.CENTER, ""+size, (int)location.getX(), (int)location.getY());
      }
    };
  }
}

