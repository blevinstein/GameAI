package com.blevinstein.boids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.swing.JPanel;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

public class SwingDraw extends DebugDraw {
  private JPanel panel;
  private Graphics2D g;

  public SwingDraw(JPanel panel) {
    super(new OBBViewportTransform());
    this.viewportTransform.setYFlip(true);
    this.viewportTransform.setExtents((float)panel.getWidth() / 2f,
        (float)panel.getHeight() / 2f);
    
    this.panel = panel;
    this.g = (Graphics2D)panel.getGraphics();
  }

  public void clear() {
    g = (Graphics2D)panel.getGraphics();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
  }

  public void drawCircle(Vec2 center, float radius, Color3f color) {
    g.setColor(convertColor(color));
    g.drawOval((int)(center.x - radius), (int)(center.y - radius),
        (int)(radius * 2), (int)(radius * 2));
  }

  public void drawPoint(Vec2 point, float radiusOnScreen, Color3f color) {
    g.setColor(convertColor(color));
    g.fillOval((int)(point.x - radiusOnScreen), (int)(point.y - radiusOnScreen),
        (int)(radiusOnScreen * 2), (int)(radiusOnScreen * 2));
  }

  public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
    g.setColor(convertColor(color));
    g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
  }

  public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
    g.setColor(convertColor(color));
    g.drawOval((int)(center.x - radius), (int)(center.y - radius), (int)(radius * 2), (int)(radius * 2));
    g.drawLine((int)(center.x + axis.x * radius), (int)(center.y + axis.y * radius),
      (int)center.x, (int)center.y);
  }

  public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
    g.setColor(convertColor(color));
    int x[] = new int[vertexCount], y[] = new int[vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      x[i] = (int)vertices[i].x;
      y[i] = (int)vertices[i].y;
    }
    Polygon polygon = new Polygon(x, y, vertexCount);
    g.drawPolygon(polygon);
  }

  public void drawString(float x, float y, String s, Color3f color) {
    g.setColor(convertColor(color));
    g.drawString(s, x, y);
  }

  public void drawTransform(Transform xf) {
    // no-op
  }
  
  public static Color convertColor(Color3f color) {
    return new Color(color.x, color.y, color.z);
  }
}
