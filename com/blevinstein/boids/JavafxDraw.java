package com.blevinstein.boids;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

public class JavafxDraw extends DebugDraw {
  private final Canvas canvas;
  private final GraphicsContext gc;

  public JavafxDraw(Canvas canvas) {
    super(new OBBViewportTransform());
    this.viewportTransform.setYFlip(true);
    this.viewportTransform.setExtents((float)canvas.getWidth() / 2f,
        (float)canvas.getHeight() / 2f);

    this.canvas = canvas;
    this.gc = canvas.getGraphicsContext2D();
  }

  public void drawCircle(Vec2 center, float radius, Color3f color) {
    gc.setStroke(convertColor(color));
    gc.strokeOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
  }

  public void drawPoint(Vec2 point, float radiusOnScreen, Color3f color) {
    gc.setFill(convertColor(color));
    gc.fillOval(point.x - radiusOnScreen, point.y - radiusOnScreen,
        radiusOnScreen * 2, radiusOnScreen * 2);
  }

  public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
    gc.setStroke(convertColor(color));
    gc.strokeLine(p1.x, p1.y, p2.x, p2.y);
  }

  public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
    gc.setStroke(convertColor(color));
    gc.strokeOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
    gc.strokeLine(center.x + axis.x * radius, center.y + axis.y * radius, center.x, center.y);
  }

  public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
    gc.setStroke(convertColor(color));
    double x[] = new double[vertexCount], y[] = new double[vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      x[i] = vertices[i].x;
      y[i] = vertices[i].y;
    }
    gc.strokePolygon(x, y, vertexCount);
  }

  public void drawString(float x, float y, String s, Color3f color) {
    gc.setStroke(convertColor(color));
    gc.fillText(s, x, y);
  }

  public void drawTransform(Transform xf) {
    // no-op
  }
  
  public static Color convertColor(Color3f color) {
    return Color.color(color.x, color.y, color.z);
  }
}
