/*
 * Copyright (c) 2012 matheusdev
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
package org.matheusdev.ddm.entity;


import org.matheusdev.util.SpriteAnimation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * @author matheusdev
 *
 */
public class EntityPlayer extends Entity {

	public static final int DOWN = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int UP = 3;

	private final float speed = 2000f * 0.016f;
	private final SpriteAnimation[] walk;
	private final Sprite sprite;
	private int direction;

	/**
	 * @param body
	 * @param sprites
	 */
	public EntityPlayer(float x, float y, EntityManager entityManager) {
		super(createCircularBody(x, y, 0.49f, 0.1f, 0.9f, 1f, entityManager.getPhysics()));
		uploadAsUserData(body);
		body.setFixedRotation(true);
		walk = new SpriteAnimation[4];
		walk[DOWN ] = entityManager.getResources().getAnimation("walking-down");
		walk[LEFT ] = entityManager.getResources().getAnimation("walking-left");
		walk[RIGHT] = entityManager.getResources().getAnimation("walking-right");
		walk[UP   ] = entityManager.getResources().getAnimation("walking-up");
		sprite = new Sprite(entityManager.getResources().getRegion("standing-down"));
	}

	/* (non-Javadoc)
	 * @see org.matheusdev.ddm.collision.Collidable#collide(com.badlogic.gdx.physics.box2d.Fixture, com.badlogic.gdx.physics.box2d.Contact, com.badlogic.gdx.physics.box2d.Manifold)
	 */
	@Override
	public void collide(Fixture other, Contact contact, Manifold manifold) {
	}

	@Override
	public void tick(EntityManager manager, float delay) {
		Vector2 linVel = body.getLinearVelocity();
		boolean moving = false;
		if (linVel.len() < 5f) {
			if (Gdx.input.isKeyPressed(Keys.D)) {
				body.applyForceToCenter(speed, 0);
				moving = true;
				direction = RIGHT;
			}
			if (Gdx.input.isKeyPressed(Keys.A)) {
				body.applyForceToCenter(-speed, 0);
				moving = true;
				direction = LEFT;
			}
			if (Gdx.input.isKeyPressed(Keys.W)) {
				body.applyForceToCenter(0, speed);
				moving = true;
				direction = UP;
			}
			if (Gdx.input.isKeyPressed(Keys.S)) {
				body.applyForceToCenter(0, -speed);
				moving = true;
				direction = DOWN;
			}
		}
		if (!moving) {
			body.setLinearVelocity(linVel.div(1.5f));
		}
		for (SpriteAnimation anim : walk) {
			anim.tick(moving ? 0.016f : 0f);
		}
	}

	/* (non-Javadoc)
	 * @see org.matheusdev.ddm.entity.Entity#draw(org.matheusdev.ddm.entity.EntityHandler, com.badlogic.gdx.graphics.g2d.SpriteBatch)
	 */
	@Override
	public void draw(EntityManager manager, SpriteBatch batch) {
		sprite.setRegion(walk[direction].getCurrentKeyframe());
		draw(sprite, body, 1f, batch);
	}
}
