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

    assertEquals(new Point(), route.position(0.0));
    assertEquals(a, route.position(3.0));
    assertEquals(b, route.position(8.0));
    assertEquals(new Point(), route.position(12.0));

    assertEquals(new Point(1.0, 0.0), route.direction(1.0));
    assertEquals(new Point(0.0, -1.0), route.direction(11.0));
  }

  @Test
  public void testShortest() {
    City from = new City(new Point(0.0, 12.0), 5.0);
    City to = new City(new Point(9.0, 0.0), 5.0);
    SimpleRoute route = SimpleRoute.shortest(from, to);
    assertEquals(2, route.getPointCount());
    assertEquals(new Point(3.0, 8.0), route.getPoint(0));
    assertEquals(new Point(6.0, 4.0), route.getPoint(1));
  }
}
