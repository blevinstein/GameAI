package com.blevinstein.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.function.Supplier;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Tests {
  /*
   * Generates a set of data and performs a one-sample two-tailed T-test.
   * Tests whether the average of the data is equal to the supplied average.
   * alpha = false positive rate, e.g. alpha = 0.05 = 1 in 20 false positives
   * Because this is inherently flaky, it is best combined with assertAtLeast
   */
  public static void assertAverage(double average, double alpha, int n, Supplier<Double> sampler) {
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

  /*
   * Runs test fragment m times, expects at least n successes.
   */
  public static void assertAtLeast(int n, int m, Runnable test) {
    int success = 0;
    for (int i = 0; i < m; i++) {
      if (!getFails(() -> test.run())) {
        success++;
      }
    }
    assertTrue(String.format("Expected %d out of %d successes, but saw %d", n, m, success),
        success >= n);
  }

  /*
   * Runs a test fragment, and expects it to throw an AssertionError.
   */
  public static void assertFails(Runnable runnable) {
    try {
      runnable.run();
    } catch (AssertionError e) {
      return;
    }
    fail("Expected test to fail");
  }

  /*
   * Runs a test fragment, and returns true if it throws an AssertionError.
   */
  public static boolean getFails(Runnable runnable) {
    try {
      runnable.run();
    } catch (AssertionError e) {
      return true;
    }
    return false;
  }

  /*
   * Runs "step" function repeatedly, and expects "condition" to become true within "timeout"
   * iterations.
   */
  public static void assertConverges(Runnable step, Supplier<Boolean> condition, int timeout) {
    for (int i = 0; i < timeout; i++) {
      step.run();
      if (condition.get()) {
        return;
      }
    }
    fail(String.format("State failed to converge after %d iterations", timeout));
  }
}
