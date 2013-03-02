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
package org.matheusdev.ror.entity;

import net.indiespot.continuations.VirtualRunnable;

import org.matheusdev.ror.collision.Collidable;
import org.matheusdev.ror.collision.Physics;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape.Type;

/**
 * @author matheusdev
 *
 */
public abstract class Entity implements Collidable, VirtualRunnable, Comparable<Entity> {
	private static final long serialVersionUID = 8198715058953169103L;

	protected static final Body createCircularBody(float x, float y, float radius,
			float restitution, float linearDamping, float density, Physics physics) {
		// Create the movable Box2D body (has position, velocity, etc.):
		BodyDef bodyDef = new BodyDef();
		bodyDef.linearDamping = linearDamping;
		bodyDef.angularDamping = 0.9f;
		bodyDef.type = BodyType.DynamicBody;
		Body body = physics.getWorld().createBody(bodyDef);

		// Create the fixture (shape) of the Body:
		FixtureDef fixDef = new FixtureDef();
		fixDef.restitution = restitution;
		fixDef.density = density;
		// Create a Bouncy ball in Box2D:
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		fixDef.shape = circle;
		// Attach the defined fixture to the body by creating it... seems legit.
		body.createFixture(fixDef);
		body.setTransform(x, y, 0);

		return body;
	}

	protected final Body body;

	protected boolean dead;

	public Entity(Body body) {
		this.body = body;
	}

	protected final void uploadAsUserData(Body body) {
		body.setUserData(this);
		for (Fixture fix : body.getFixtureList()) {
			fix.setUserData(this);
		}
	}

	public Body getBody() {
		return body;
	}

	public abstract void draw(EntityManager handler, SpriteBatch batch);

	public final boolean isDead() {
		return dead;
	}

	public final void die() {
		dead = true;
	}

	public void draw(Sprite sprite, Body body, float width, SpriteBatch batch) {
		draw(sprite, body, width, 0, 0, batch);
	}

	public void draw(Sprite sprite, Body body, float width, float offsetX, float offsetY, SpriteBatch batch) {
		// Super-Duper important Box2D-Magic code:
		// This should be / is in almost every Box2D project
		// It takes the Body and the associated sprite and
		// renders the sprite properly, using the body's
		// position, rotation and origin.
		final float worldToSprite = sprite.getWidth() / width;
		final float spriteToWorld = width / sprite.getWidth();
		// Get body position:
		final float bodyX = body.getPosition().x;
		final float bodyY = body.getPosition().y;
		// Get body center:
		final Vector2 center = body.getLocalCenter();
		final Vector2 massCenter = body.getMassData().center;
		center.sub(massCenter).add(offsetX, offsetY);
		// Compute sprite-space center:
		final Vector2 spriteCenter = new Vector2(sprite.getWidth()/2, sprite.getHeight()/2).sub((center.cpy().mul(worldToSprite)));
		// Upload to sprite:
		sprite.setScale(1f * spriteToWorld);
		sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
		sprite.setOrigin(spriteCenter.x, spriteCenter.y);
		sprite.setPosition(
				bodyX - spriteCenter.x,
				bodyY - spriteCenter.y);
		// Draw Sprite:
		sprite.draw(batch);
	}

	protected final float getCircleRadius(Body body) {
		if (body.getFixtureList() == null || body.getFixtureList().size() < 1) {
			return 0f;
		}
		Fixture fix = body.getFixtureList().get(0);
		if (fix.getShape().getType() == Type.Circle) {
			return fix.getShape().getRadius();
		} else {
			return 0f;
		}
	}

	@Override
	public int compareTo(Entity e) {
		float eY = e.getBody().getPosition().y;
		float y = getBody().getPosition().y;

		if (eY > y) {
			return 1;
		} else if (eY < y) {
			return -1;
		} else {
			return 0;
		}
	}

}
