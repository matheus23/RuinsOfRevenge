/*
 * Copyright (c) 2013 matheusdev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.matheusdev.ror.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author matheusdev
 *
 */
public class Physics implements ContactListener, Disposable {

	protected final World world;
	public float timescale = 1f;

	public Physics(Vector2 gravity, boolean sleep) {
		world = new World(gravity, sleep);
		world.setContactListener(this);
	}

	public World getWorld() {
		return world;
	}

	public void step(float delta) {
		world.step(Math.min(delta, 0.02f) * timescale, 4, 4);
	}

	public void step() {
		step(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() {
		world.dispose();
	}

	public Body createBox(BodyType type, float width, float height, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body body = world.createBody(def);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, height);
		body.createFixture(shape, density);
		shape.dispose();

		return body;
	}

	public Body createCircle(BodyType type, float radius, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body body = world.createBody(def);

		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		body.createFixture(shape, density);
		shape.dispose();
		MassData mass = new MassData();
		mass.mass = 1000f;
		body.setMassData(mass);

		return body;
	}

	public Body createEdge(BodyType type, float x0, float y0, float x1, float y1, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body body = world.createBody(def);

		EdgeShape shape = new EdgeShape();
		shape.set(new Vector2(0, 0), new Vector2(x1 - x0, y1 -y0));
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.friction = 1;
		fdef.density = density;
		body.createFixture(fdef);
		body.setTransform(x0, y0, 0);
		shape.dispose();

		return body;
	}

	public Body createPolygon(BodyType type, float x, float y, Vector2[] vertices, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body body = world.createBody(def);

		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.friction = 1;
		fdef.density = density;
		body.createFixture(fdef);
		body.setTransform(x, y, 0f);
		shape.dispose();

		return body;
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#beginContact(com.badlogic.gdx.physics.box2d.Contact)
	 */
	@Override
	public void beginContact(Contact c) {
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#endContact(com.badlogic.gdx.physics.box2d.Contact)
	 */
	@Override
	public void endContact(Contact c) {
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#postSolve(com.badlogic.gdx.physics.box2d.Contact, com.badlogic.gdx.physics.box2d.ContactImpulse)
	 */
	@Override
	public void postSolve(Contact c, ContactImpulse ci) {
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#preSolve(com.badlogic.gdx.physics.box2d.Contact, com.badlogic.gdx.physics.box2d.Manifold)
	 */
	@Override
	public void preSolve(Contact c, Manifold m) {
		Fixture fixtureA = c.getFixtureA();
		Fixture fixtureB = c.getFixtureB();

		handleFixture(fixtureA, fixtureB, c, m);
		handleFixture(fixtureB, fixtureA, c, m);
	}

	public void handleFixture(Fixture fixture, Fixture other, Contact c, Manifold m) {
		Object obj = fixture.getUserData();
		if (obj instanceof Collidable) {
			((Collidable) obj).collide(other, c, m);
		}
	}

}
