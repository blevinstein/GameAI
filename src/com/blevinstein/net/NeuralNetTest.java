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
    assertTrue(net2.getLayer(1).getRowDimension() == 5 + 1);
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
    NeuralNet net = new NeuralNet(ImmutableList.of(identity, identity));
    Signal input = new Signal(ImmutableList.of(-1.0, 1.0));
    Signal output = net.apply(input);
    assertEquals(input.sigmoid().sigmoid(), output);
  }

  @Test
  public void testPropagate_nonSquare() {
    RealMatrix nonSquare = new Array2DRowRealMatrix(new double[][]{
        new double[]{1, 0, 0, 0},
        new double[]{0, 1, 0, 0},
        new double[]{0, 0, 0, 1}});
    NeuralNet net = new NeuralNet(ImmutableList.of(nonSquare, nonSquare.transpose()));
    Signal input = new Signal(ImmutableList.of(-1.0, 1.0));
    Signal output = net.apply(input);
    assertEquals(input.sigmoid().sigmoid(), output);
  }

  @Test
  public void testBackpropagate_whenCorrect() {
    RealMatrix identity = new Array2DRowRealMatrix(new double[][]{
        new double[]{1, 0, 0},
        new double[]{0, 1, 0},
        new double[]{0, 0, 1}});
    NeuralNet net = new NeuralNet(ImmutableList.of(identity, identity));
    Signal input = new Signal(ImmutableList.of(-1.0, 1.0));
    Signal target = input.sigmoid().sigmoid();
    net.backpropagate(input, target);
    // net should not change because the output is correct
    assertEquals(new NeuralNet(ImmutableList.of(identity, identity)),
        net);
  }

  @Test
  public void testBackpropagate_whenIncorrect() {
    RealMatrix identity = new Array2DRowRealMatrix(new double[][]{
        new double[]{1, 0, 0},
        new double[]{0, 1, 0},
        new double[]{0, 0, 1}});
    NeuralNet net = new NeuralNet(ImmutableList.of(identity, identity));

    Signal input = new Signal(ImmutableList.of(-1.0, 1.0));
    Signal target = new Signal(ImmutableList.of(1.0, -1.0));

    Signal output1 = net.apply(input);
    net.backpropagate(input, target);
    Signal output2 = net.apply(input);

    // output should get closer to the target after running backprop
    assertTrue(output2.dist(target) < output1.dist(target));
  }

  @Test
  public void testBackpropagate_converge() {
    RealMatrix identity = new Array2DRowRealMatrix(new double[][]{
        new double[]{1, 0, 0},
        new double[]{0, 1, 0},
        new double[]{0, 0, 1}});
    NeuralNet net = new NeuralNet(ImmutableList.of(identity, identity));

    Signal input = new Signal(ImmutableList.of(-1.0, 1.0));
    Signal target = new Signal(ImmutableList.of(1.0, -1.0));

    for (int i = 0; i < 1000; i++) {
      net.backpropagate(input, target);
    }
    Signal output = net.apply(input);

    // output should approach the target after many iterations
    assertTrue(output.dist(target) < 0.01);
  }

  @Test
  public void testBackpropagate_nonSquare() {
    RealMatrix nonSquare = new Array2DRowRealMatrix(new double[][]{
        new double[]{1, 0, 0, 0},
        new double[]{0, 1, 0, 0},
        new double[]{0, 0, 0, 1}});
    NeuralNet net = new NeuralNet(ImmutableList.of(nonSquare, nonSquare.transpose()));

    Signal input = new Signal(ImmutableList.of(-1.0, 1.0));
    Signal target = new Signal(ImmutableList.of(1.0, -1.0));

    Signal output1 = net.apply(input);
    for (int i = 0; i < 10; i++) {
      net.backpropagate(input, target);
    }
    Signal output2 = net.apply(input);

    // output should get closer to the target after running backprop
    assertTrue(output2.dist(target) < output1.dist(target));
  }
}
