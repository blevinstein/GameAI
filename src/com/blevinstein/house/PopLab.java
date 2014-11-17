package com.blevinstein.house;

import com.blevinstein.net.BinaryConverter;
import com.blevinstein.net.NetPopulation;
import com.blevinstein.net.NeuralNet;
import com.blevinstein.util.Throttle;
import com.blevinstein.util.Util;
import com.blevinstein.util.Util.Align;

import com.google.common.collect.ImmutableList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Function;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JPanel;

// This lab helps visualize the progress of an evolving population.

class PopLab extends JPanel implements KeyListener {

  private String HELP =
    "Choose a function for the population to learn. " +
    "Presse E to start/stop evolution. ";

  private final int POPULATION_SIZE = 100;

  private SimplePopulation population;

  private Map<String, Function<List<Boolean>, Boolean>> functions = new HashMap<>();
  private JComboBox<String> selectFunction;

  public PopLab() {
    super(null); // no layout manager

    // receive key events
    this.setFocusable(true);
    this.addKeyListener(this);

    // add combo box for selecting functions to learn
    selectFunction = new JComboBox<String>();
    selectFunction.setFocusable(false);
    selectFunction.setBounds(10, 10, 200, 25);
    this.add(selectFunction);

    addFunction("XOR", inputs ->
        inputs.get(0) ^ inputs.get(1));
    addFunction("A", inputs ->
        inputs.get(0));
    addFunction("B", inputs ->
        inputs.get(1));
    addFunction("AND", inputs ->
        inputs.get(0) && inputs.get(1));
    addFunction("OR", inputs ->
        inputs.get(0) || inputs.get(1));

    setFunction(functions.get(selectFunction.getItemAt(0)));
    selectFunction.addActionListener(e ->
                                     setFunction(functions.get(
                                           selectFunction.getItemAt(
                                               selectFunction.getSelectedIndex()))));

    population = new SimplePopulation(POPULATION_SIZE);
  }

  private void addFunction(String name, Function<List<Boolean>, Boolean> function) {
    selectFunction.addItem(name);
    functions.put(name, function);
  }

  private Function<List<Boolean>, Boolean> currentFunction;
  private void setFunction(Function<List<Boolean>, Boolean> f) {
    currentFunction = f;
  }

  boolean evolving = false;
  public void run() {
    Throttle t = new Throttle(60); // limit framerate
    Throttle t2 = new Throttle(4); // limit epochs/second
    while (true) {
      if (evolving) {
        population.evolve();
        t2.sleep();
      }
      repaint();
      t.sleep();
    }
  }

  private boolean displayHelp = false;
  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    // draw help
    if (displayHelp) {
      Util.placeText(g, Align.SE, HELP, getWidth() - 20, getHeight() - 20);
    } else {
      Util.placeText(g, Align.SE, "H for help", getWidth() - 20, getHeight() - 20);
    }
  }

  @SuppressWarnings("unchecked")
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_E:
        evolving = !evolving;
        break;
      case KeyEvent.VK_H:
        displayHelp = true;
        break;
      case KeyEvent.VK_ESCAPE:
        System.exit(0);
        break;
    }
  }
  public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_H:
        displayHelp = false;
        break;
    }
  }
  public void keyTyped(KeyEvent e) {}

  // Represents a population of 2-input 1-output neural networks learning a function
  public static class SimplePopulation extends NetPopulation {
    private Function<List<Boolean>, Boolean> function;

    public SimplePopulation(int size) {
      super(size, () -> NeuralNet.create(ImmutableList.of(2, 2, 1)));
    }
    
    public void setFunction(Function<List<Boolean>, Boolean> function) {
      this.function = function;
    }

    // TODO(blevinstein): implement ListConverter somehow, and fix this
    @Override
    public double getFitness(NeuralNet individual) {
      NeuralNet.NetAdapter<List<Boolean>, List<Boolean>> adapter = individual.getAdapter(
          new BinaryConverter(), new BinaryConverter().reverse());
      List<List<Boolean>> cases = ImmutableList.of(ImmutableList.of(false, false),
          ImmutableList.of(false, true),
          ImmutableList.of(true, false),
          ImmutableList.of(true, true));
      double score = 0.0;
      for (List<Boolean> kase : cases) {
        List<Boolean> expected = ImmutableList.of(function.apply(kase));
        List<Boolean> actual = adapter.netApply(kase);
        if (actual.equals(expected)) { score += 1; }
      }
      return score;
    }
  }

  public static final long serialVersionUID = 1;
}
