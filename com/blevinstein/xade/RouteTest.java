package com.blevinstein.xade;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;

public class RouteTest {
  @Test
  public void testSimpleRoute() {
    // triangle route, starts and ends at origin
    Point a = new Point(3.0, 0.0);
    Point b = new Point(0.0, 4.0);
    List<Point> points = ImmutableList.of(new Point(), a, b, new Point());
    Route route = new SimpleRoute(points);

    assertEquals(12.0, route.length(), 0.001);
    assertEquals(new Point(), route.get(0.0));
    assertEquals(a, route.get(3.0));
    assertEquals(b, route.get(8.0));
    assertEquals(new Point(), route.get(12.0));
  }
}
