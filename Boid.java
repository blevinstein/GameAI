import java.awt.Graphics;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Boid {
  private static final double MAX_ACCEL = 1.0;
  // NOTE: no maximum velocity
  
  private final double _radius = 1.0;

  private Vector2D _position;
  public Vector2D position() { return _position; }

  private Vector2D _velocity;
  public Vector2D velocity() { return _velocity; }

  private double _score = 0.0;
  public double score() { return _score; }

  public Boid(Vector2D position, Vector2D velocity) {
    _position = position;
    _velocity = velocity;
  }

  // updates and returns position
  public Vector2D step(Vector2D acceleration) {
    // magnitude of acceleration <= MAX_ACCEL
    double mag = acceleration.getNorm();
    if (mag > MAX_ACCEL) {
      acceleration = acceleration.scalarMultiply(MAX_ACCEL / mag);
    }

    // update velocity and position
    Vector2D newVelocity = _velocity.add(acceleration);
    Vector2D effectiveVelocity = _velocity.add(newVelocity).scalarMultiply(0.5);
    _velocity = newVelocity;
    _position = _position.add(effectiveVelocity);

    return _position;
  }

  public void draw(Graphics g, int ix, int iy, int sx, int sy) {
    // TODO: draw the boid
  }
}
