package com.blevinstein.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;

public class NeuralNetTest {
  @Test
  public void testCreate() {
    NeuralNet net = NeuralNet.create(ImmutableList.of(2, 4));
    assertEquals(2, net.getInputs());
    assertEquals(4, net.getOutputs());

    NeuralNet net2 = NeuralNet.create(ImmutableList.of(3, 5, 7));
    assertEquals(3, net2.getInputs());
    assertTrue(net2.getLayer(0).getColumnDimension() == 5 + 1);
    assertEquals(7, net2.getOutputs());
  }

  @Test
  public void testAffinize() {
    RealMatrix random = NeuralNet.newMatrix(3, 3);
    RealMatrix affinized = NeuralNet.affinize(random);
    assertEquals(new ArrayRealVector(new double[]{0, 0, 1}),
        affinized.getColumnVector(affinized.getColumnDimension() - 1));
  }

  @Test
  public void testNormalize() {
    RealMatrix random = NeuralNet.newMatrix(5, 5);
    double norm = NeuralNet.normalize(random).getFrobeniusNorm();

    // Scramble and normalize again
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        random.setEntry(i, j, Math.random() < 0.5 ? 10 : -50);
      }
    }
    assertEquals(NeuralNet.normalize(random).getFrobeniusNorm(), norm, 0.01);
  }

  @Test
  public void testPropagate() {
    RealMatrix identity = new Array2DRowRealMatrix(new double[][]{
        new double[]{1, 0, 0},
        new double[]{0, 1, 0},
        new double[]{0, 0, 1}});
  }
}
