package com.blevinstein.xade;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.apache.commons.lang3.tuple.Pair;

public class MouseStrategy extends ManualStrategy implements MouseListener, MouseMotionListener {
  private Camera camera;
  private City selected = null;

  public MouseStrategy(World world, Player player, Camera camera) {
    super(world, player);
    this.camera = camera;
  }

  @Override public void mouseClicked(MouseEvent e) {
    Point point = positionOf(e);
    Pair<City, Double> closest = world.closestCity(point);
    if (closest.getLeft() != null && closest.getRight() < 0.0) {
      // User clicked on a City
      if (selected == null) {
        selected = closest.getLeft();
      } else {
        Move move = new Move(1, selected, closest.getLeft());
        makeMove(move);
      }
    } else {
      // User clicked elsewhere
      selected = null;
    }
  }
  @Override public void mouseEntered(MouseEvent e) {}
  @Override public void mouseExited(MouseEvent e) {}
  @Override public void mousePressed(MouseEvent e) {}
  @Override public void mouseReleased(MouseEvent e) {}

  @Override public void mouseMoved(MouseEvent e) {}
  @Override public void mouseDragged(MouseEvent e) {}

  private Point positionOf(MouseEvent e) {
    return camera.applyInverse(new Point(e.getX(), e.getY()));
  }
}
