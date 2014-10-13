package com.blevinstein.boids;

import com.blevinstein.util.Throttle;

import java.awt.Graphics2D;
import javax.swing.JPanel;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class BoidWorld {

  private World world;
  private Throttle throttle;
  private SwingDraw draw;

  private Body boidBody;

  public BoidWorld(int fps, JPanel panel) {
    draw = new SwingDraw(panel);
    throttle = new Throttle(fps);
    world = new World(new Vec2(0f, 0f));
    world.setDebugDraw(draw);

    boidBody = world.createBody(boidDef(new Vec2(0f, 0f)));
    boidBody.createFixture(boidFixtureDef());
  }

  public void mainloop() {
    while (true) {
      System.out.println(boidBody.getPosition());
      draw.clear();
      world.step(1f/60, 8, 3);
      world.drawDebugData();
      throttle.sleep();
    }
  }

  public BodyDef boidDef(Vec2 position) {
    BodyDef def = new BodyDef();
    def.active = true;
    def.allowSleep = true;
    def.angle = 0f;
    def.angularDamping = 0.01f;
    def.angularVelocity = 0f;
    def.awake = true;
    def.bullet = true;
    def.fixedRotation = false;
    def.gravityScale = 1f;
    def.linearDamping = 0f;
    def.linearVelocity = new Vec2(0f, 0f);
    def.position = position;
    def.type = BodyType.DYNAMIC;
    def.userData = null;
    return def;
  }

  public FixtureDef boidFixtureDef() {
    FixtureDef def = new FixtureDef();
    def.density = 1f;
    def.friction = 0.1f;
    def.isSensor = false;
    def.restitution = 0.1f;
    Vec2[] points = {new Vec2(1.5f, 0f),
                     new Vec2(-1.5f, 1f),
                     new Vec2(-0.5f, 0f),
                     new Vec2(-1.5f, -1f)};
    PolygonShape shape = new PolygonShape();
    shape.set(points, points.length);
    def.shape = shape;
    def.userData = null;
    return def;
  }
}
