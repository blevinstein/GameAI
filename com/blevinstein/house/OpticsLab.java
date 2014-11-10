package com.blevinstein.house;

import com.blevinstein.image.ImageClassifier;
import com.blevinstein.image.ValueChannel;
import com.blevinstein.net.LetterConverter;
import com.blevinstein.net.NeuralNet2;
import com.blevinstein.util.Json;
import com.blevinstein.util.Throttle;
import com.blevinstein.util.Util;
import com.blevinstein.util.Util.Align;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

// This lab is an experiment in image classification.
// I'm trying to learn to classify letters using many different fonts.
//
// TODO: Separate training and verification sets. Maybe fonts A-M and N-Z?

class OpticsLab extends JPanel implements KeyListener {

  private String HELP =
    "Press S to save, L to load a saved net. " +
    "T to start/stop training of the net. " +
    "R to reset correct % stats. ";

  private ImageClassifier<String> _classifier =
    new ImageClassifier<>(new ValueChannel(4, 4), new LetterConverter());

  private BufferedImage image = null;
  private String guess = "?";
  /*
  private Font[] allFonts =
    GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
  */
  private String[] allFonts = {"Arial", "Times New Roman"};
  private Supplier<Pair<BufferedImage, String>> testCaseGenerator = () -> {
    // pick a number 0-25
    int num = (int)(Math.random() * 26);
    // get the corresponding letter
    String letter = LetterConverter.LETTERS.charAt(num) + "";

    // TODO: choose a random font FROM A SCREENED LIST
    //String font = allFonts[(int)(allFonts.length * Math.random())].getFontName();
    String font = allFonts[(int)(allFonts.length * Math.random())];

    // create an image
    image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();
    // clear the image
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, image.getWidth(), image.getHeight());
    // draw the letter
    g.setColor(Color.WHITE);
    g.setFont(new Font(font, Font.PLAIN, 30));
    //g.setFont(new Font("Arial", Font.PLAIN, 34));
    Util.placeText(g, Align.CENTER, letter,
    image.getWidth() / 2, image.getHeight() / 2);
    /*
    image.getWidth()/2 + (int)(image.getWidth()/4*Util.random()),
    image.getHeight()/2 + (int)(image.getHeight()/4*Util.random()));
    */

    // return the image and letter
    return new ImmutablePair<>(image, letter);
  };

  public OpticsLab() {
    super(null); // no layout manager

    // receive key events
    this.setFocusable(true);
    this.addKeyListener(this);
  }

  private void trainRandom() {
    // generate an image-letter combination
    Pair<BufferedImage, String> testCase = testCaseGenerator.get();
    image = testCase.getLeft();
    String target = testCase.getRight();

    // query the network
    guess = _classifier.process(image);

    // then give it the correct answer
    _classifier.backpropagate(image, target);

    // tabulate correct/incorrect answers
    if (guess.equals(target)) {
      correct++;
    } else {
      incorrect++;
    }
    correctPercent = correct * 100.0 / (correct + incorrect);

    repaint();
  }

  boolean training = false;
  public void run() {
    Throttle t = new Throttle(100);
    while (true) {
      if (training) { trainRandom(); }
      repaint();
      t.sleep();
    }
  }

  private double correctPercent = 0.0;
  private int correct = 0, incorrect = 0;
  private boolean displayHelp = false;
  public void paintComponent(Graphics g) {
    // clear the screen
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    // image in the top left
    if (image != null) {
      double scale = Math.min( (getWidth() / 2 - 20) / image.getWidth(),
                               (getHeight() / 2 - 20) / image.getHeight() );
      int w = (int)(scale * image.getWidth());
      int h = (int)(scale * image.getHeight());
      // draw image with border
      g.setColor(Color.BLACK);
      g.drawRect(getWidth() / 4 - w / 2 - 1, getHeight() / 4 - h / 2 - 1,
                 w + 2, h + 2);
      g.drawImage(image,
                  getWidth() / 4 - w / 2,
                  getHeight() / 4 - h / 2,
                  w, h, null);

      // network on the right
      _classifier.drawState(g, image,
                            getWidth() / 2 + 10, 10,
                            getWidth() / 2 - 20, getHeight() - 20);
    }

    g.setColor(Color.BLACK);

    // letter in the bottom left
    g.setFont(new Font("Arial", Font.PLAIN, 100));
    Util.placeText(g, Align.CENTER, guess, getWidth() / 4, getHeight() * 3 / 4);

    g.setFont(new Font("Arial", Font.PLAIN, 15));

    // draw success % since last reset
    Util.placeText(g, Align.NE,
                   String.format("Success: %2.2f%%", correctPercent),
                   getWidth() - 20, 20);

    // draw help
    if (displayHelp) {
      Util.placeText(g, Align.SE, HELP, getWidth() - 20, getHeight() - 20);
    } else {
      Util.placeText(g, Align.SE, "H for help", getWidth() - 20, getHeight() - 20);
    }
  }

  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_R:
        correct = incorrect = 0;
        break;
      case KeyEvent.VK_S:
        Json.save(_classifier.getNet(), "classifier.json");
        break;
      case KeyEvent.VK_L:
        NeuralNet2 newNet = Json.load("classifier.json", NeuralNet2.class);
        if (newNet != null) { _classifier.setNet(newNet); }
        break;
      case KeyEvent.VK_T:
        training = !training;
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

  private static final long serialVersionUID = 1;
}
