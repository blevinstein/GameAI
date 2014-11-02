package com.blevinstein.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.function.Supplier;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Tests {
  public static void assertAverage(Supplier<Double> sampler, double average, double alpha, int n) {
    double results[] = new double[n];
    for (int i = 0; i < n; i++) {
      results[i] = sampler.get();
    }
    DescriptiveStatistics stats = new DescriptiveStatistics(results);

    // One-sample two-tailed T-test
    double t = (stats.getMean() - average) / (stats.getVariance() / Math.sqrt(n));
    TDistribution distribution = new TDistribution(n - 1);
    double p = distribution.cumulativeProbability(t);
    if (p < alpha / 2) {
      fail(String.format("Average %f is less than %f with alpha = %f",
          stats.getMean(), average, alpha));
    } else if(p > 1 - alpha / 2) {
      fail(String.format("Average %f is greater than %f with alpha = %f",
            stats.getMean(), average, alpha));
    }
  }

  public static void assertAtLeast(Supplier<Boolean> test, int n, int m) {
    int success = 0;
    for (int i = 0; i < m; i++) {
      if (test.get()) {
        success++;
      }
    }
    assertTrue(String.format("Expected %d out of %d successes, but saw %d", n, m, success),
        success >= n);
  }

  public static void assertFails(Runnable runnable) {
    try {
      runnable.run();
    } catch (AssertionError e) {
      return;
    }
    fail("Expected test to fail");
  }

  public static boolean getFails(Runnable runnable) {
    try {
      runnable.run();
    } catch (AssertionError e) {
      return true;
    }
    return false;
  }
}
