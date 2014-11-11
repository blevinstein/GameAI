package com.blevinstein.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class NeuralNetTest {
  @Test
  public void testConstructor() {
    NeuralNet net = NeuralNet.create(ImmutableList.of(2, 4));
    assertEquals(2, net.getInputs());
    assertEquals(4, net.getOutputs());

    NeuralNet net2 = NeuralNet.create(ImmutableList.of(3, 5, 7));
    assertEquals(3, net2.getInputs());
    assertTrue(net2.getLayer(0).getColumnDimension() == 5 + 1);
    assertEquals(7, net2.getOutputs());
  }
}
