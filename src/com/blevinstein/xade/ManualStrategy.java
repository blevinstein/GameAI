package com.blevinstein.xade;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public class ManualStrategy implements Strategy {
  protected World world;
  protected Player player;
  private List<Move> moveBuffer = new ArrayList<>();

  public ManualStrategy(World world, Player player) {
    this.world = world;
    this.player = player;
  }

  public void makeMove(Move move) {
    if (world.validMove(player, move)) {
      moveBuffer.add(move);
    }
  }

  // implements Strategy
  @Override
  public List<Move> getMoves() {
    List<Move> moves = ImmutableList.copyOf(moveBuffer);
    moveBuffer.clear();
    return moves;
  }
}
