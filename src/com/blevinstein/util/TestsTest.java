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
    int n = 100;
    NormalDistribution distribution = new NormalDistribution();

    // Expect at most 3 * alpha percentage failures
    assertAtLeast((int)(n * (1 - alpha * 3)), n, () -> {
          assertAverage(0.0, alpha, 20, () -> distribution.sample());
    });
  }

  @Test
  public void testAssertFails() {
    assertFails(() -> fail());
    assertFails(() -> assertFails(() -> {}));
  }

  @Test
  public void testAssertAtLeast() {
    final Iterator<Boolean> mask = ImmutableList.of(true, true, false).iterator();
    assertAtLeast(2, 3, () -> {
        if (!mask.next()) { fail(); }
    });

    final Iterator<Boolean> mask2 = ImmutableList.of(true, true, false).iterator();
    assertFails(() ->
        assertAtLeast(2, 3, () -> {
            if(mask2.next()) { fail(); }
        }));
  }
}
