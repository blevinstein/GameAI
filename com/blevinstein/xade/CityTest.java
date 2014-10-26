package com.blevinstein.xade;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class CityTest {
  @Test
  public void testOrbitPoints() {
    City city = new City(new Point(0.0, 0.0), 5.0);

    assertThat(
        ImmutableList.of(new Point(5.0, 0.0), new Point(-5.0, 0.0)),
        equalTo(city.orbitPoints(2)));

    assertThat(
        ImmutableList.of(new Point(5.0, 0.0), new Point(0.0, 5.0),
                         new Point(-5.0, 0.0), new Point(0.0, -5.0)),
        equalTo(city.orbitPoints(4)));
  }
}
