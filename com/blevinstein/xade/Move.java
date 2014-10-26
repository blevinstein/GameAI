package com.blevinstein.xade;

public class Move {
  private final int armies;
  private final City destination;
  private final City source;

  public Move(int armies, City destination, City source) {
    this.armies = armies;
    this.destination = destination;
    this.source = source;
  }

  public int getArmies() { return armies; }
  public City getDestination() { return destination; }
  public City getSource() { return source; }
}
