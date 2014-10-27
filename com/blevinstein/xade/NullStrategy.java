package com.blevinstein.xade;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class NullStrategy implements Strategy {
  @Override
  public List<Move> getMoves() {
    return ImmutableList.<Move>of();
  }
}
