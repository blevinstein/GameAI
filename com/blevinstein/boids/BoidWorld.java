package com.blevinstein.boids;

import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class BoidWorld extends Application {

  private Canvas canvas;
  private World world;

  @Override
  public void start(Stage stage) {
    // Setup window
    stage.setTitle("BoidWorld");
    canvas = new Canvas(600, 400);
    stage.setScene(new Scene(new Group(canvas)));

    // Setup world
    world = new World(new Vec2(0f, 0f));
    world.setDebugDraw(new JavafxDraw(canvas));

    // Show
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
