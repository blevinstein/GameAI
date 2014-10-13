package com.blevinstein.boids;

import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Boids extends Application {
  @Override
  public void start(Stage stage) {
    stage.setTitle("BoidWorld");
    Canvas canvas = new Canvas(600, 400);
    stage.setScene(new Scene(new Group(canvas)));

    BoidWorld world = new BoidWorld(canvas, 100);

    stage.show();

    world.mainloop();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
