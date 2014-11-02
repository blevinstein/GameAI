package com.blevinstein.util;

import static com.blevinstein.util.Tests.assertAtLeast;
import static com.blevinstein.util.Tests.assertAverage;
import static com.blevinstein.util.Tests.assertFails;
import static com.blevinstein.util.Tests.getFails;
import static org.junit.Assert.fail;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Test;

public class TestsTest {
  @Test
  public void testAssertAverage() {
    double alpha = 0.05;
    NormalDistribution distribution = new NormalDistribution();

    // Expect at most 2 * alpha percentage failures
    assertAtLeast(
        () -> !getFails(() -> assertAverage(() -> distribution.sample(), 0.0, alpha, 20)),
        (int)(100 * (1 - alpha * 2)), 100);
  }

  @Test
  public void testAssertFails() {
    assertFails(() -> fail());
    assertFails(() -> assertFails(() -> {}));
  }

  @Test
  public void testAssertAtLeast() {
    final Iterator<Boolean> good = ImmutableList.of(true, true, false).iterator();
    assertAtLeast(() -> good.next(), 2, 3);

    final Iterator<Boolean> bad = ImmutableList.of(true, false, false).iterator();
    assertFails(() -> assertAtLeast(() -> bad.next(), 2, 3));
  }
}
