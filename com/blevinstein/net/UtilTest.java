package com.blevinstein.net;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class UtilTest {
  @Test
  public void testChain() {
    assertEquals(
        Util.chain(ImmutableList.of(1, 2, 3)),
        ImmutableList.of(Pair.of(1, 2), Pair.of(2, 3)));

    assertEquals(
        Util.chain(ImmutableList.of(2, 4)),
        ImmutableList.of(Pair.of(2, 4)));
  }

  @Test
  public void testChain_emptyList() {
    // Empty list
    assertEquals(
        Util.chain(ImmutableList.<Integer>of()),
        ImmutableList.<Pair<Integer,Integer>>of());
    // Single element
    assertEquals(
        Util.chain(ImmutableList.of(5)),
        ImmutableList.<Pair<Integer,Integer>>of());
  }
}
