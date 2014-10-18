package com.blevinstein.xade;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PointTest {
  @Test
  public void testInterpolate() {
    Point a = new Point(1.0, 2.0);
    Point b = new Point(2.0, 3.0);
    assertEquals(a, Point.interpolate(a, b, 0.0));
    assertEquals(b, Point.interpolate(a, b, 1.0));
    assertEquals(a.add(b).times(0.5), Point.interpolate(a, b, 0.5));
  }

  @Test
  public void testMagnitude() {
    Point a = new Point(3.0, 4.0);
    assertEquals(a.mag(), 5.0, 0.001);
    
    Point normalized = a.norm();
    assertEquals(normalized.mag(), 1.0, 0.001);
  }
}
