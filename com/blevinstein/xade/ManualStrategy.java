package com.blevinstein.xade;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public class ManualStrategy implements Strategy {
  private World world;
  private List<Move> moveBuffer = new ArrayList<>();

  public ManualStrategy(World world) {
    this.world = world;
  }

  public void makeMove(Move move) {
    if (world.validMove(move)) {
      moveBuffer.add(move);
    }
  }

  // implements Strategy
  @Override
  public List<Move> getMoves(World world) {
    List<Move> moves = ImmutableList.copyOf(moveBuffer);
    moveBuffer.clear();
    return moves;
  }
}
