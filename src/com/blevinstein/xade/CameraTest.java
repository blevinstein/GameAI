package com.blevinstein.xade;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

public class CameraTest {
  public static final Point OUTPUT = new Point(640, 480);
  public static final Point INPUT = new Point(4, 3);
  public static final Point CENTER = new Point(1, 2);

  private Camera camera;

  @Before
  public void setup() {
    camera = new Camera()
      .input(INPUT.getX(), INPUT.getY())
      .output(OUTPUT.getX(), OUTPUT.getY())
      .center(CENTER);
  }

  @Test
  public void testApply() {
    // check output size
    Point min = CENTER.sub(INPUT.times(0.5));
    Point max = CENTER.add(INPUT.times(0.5));
    Point outputWidth = camera.apply(max).sub(camera.apply(min));
    assertEquals(OUTPUT, outputWidth);
    // check center of view
    System.err.println(CENTER);
    assertEquals(OUTPUT.times(0.5), camera.apply(CENTER));
  }
}
