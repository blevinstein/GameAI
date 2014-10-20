package com.blevinstein.xade;

public class Army {
  private Player player;
  private State state;
  private int count;

  // attributes
  private double speed = 1.0;
  private double strength = 1.0;

  public State getState() { return state; }
  public void setState(State state) {
    this.state = state;
  }

  public void update(double t) {
    this.state = state.update(t);
  }
}
