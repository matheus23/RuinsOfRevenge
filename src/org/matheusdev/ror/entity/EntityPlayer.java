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
package org.matheusdev.ror.entity;


import net.indiespot.continuations.VirtualThread;

import org.matheusdev.ror.entity.component.ComponentMovement;
import org.matheusdev.util.Dir;
import org.matheusdev.util.SpriteAnimation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.matthiasmann.continuations.SuspendExecution;

/**
 * @author matheusdev
 *
 */
public class EntityPlayer extends Entity {
	private static final long serialVersionUID = 9012418973465053432L;

	private final float speed = 16f;
	private final SpriteAnimation[] walk;
	private final TextureRegion[] stand;
	private final Sprite sprite;
	private final ComponentMovement movement;

	/**
	 * @param body
	 * @param sprites
	 */
	public EntityPlayer(float x, float y, EntityManager entityManager) {
		super(createCircularBody(x, y, 0.30f, 0.1f, 0.9f, 1f, entityManager.getPhysics()));
		uploadAsUserData(body);
		body.setFixedRotation(true);
		walk = new SpriteAnimation[4];
		walk[Dir.DOWN ] = entityManager.getResources().getAnimation("walking-down");
		walk[Dir.LEFT ] = entityManager.getResources().getAnimation("walking-left");
		walk[Dir.RIGHT] = entityManager.getResources().getAnimation("walking-right");
		walk[Dir.UP   ] = entityManager.getResources().getAnimation("walking-up");
		stand = new TextureRegion[4];
		stand[Dir.DOWN ] = entityManager.getResources().getRegion("standing-down");
		stand[Dir.LEFT ] = entityManager.getResources().getRegion("standing-left");
		stand[Dir.RIGHT] = entityManager.getResources().getRegion("standing-right");
		stand[Dir.UP   ] = entityManager.getResources().getRegion("standing-up");
		sprite = new Sprite(stand[Dir.DOWN]);
		movement = new ComponentMovement(Dir.DOWN);
	}

	/* (non-Javadoc)
	 * @see org.matheusdev.ddm.collision.Collidable#collide(com.badlogic.gdx.physics.box2d.Fixture, com.badlogic.gdx.physics.box2d.Contact, com.badlogic.gdx.physics.box2d.Manifold)
	 */
	@Override
	public void collide(Fixture other, Contact contact, Manifold manifold) {
	}

	@Override
	public void run() throws SuspendExecution {
		while (true) {
			float xsteer = 0f;
			float ysteer = 0f;

			if (Gdx.input.isKeyPressed(Keys.W)) {
				ysteer += 1f;
			}
			if (Gdx.input.isKeyPressed(Keys.S)) {
				ysteer -= 1f;
			}
			if (Gdx.input.isKeyPressed(Keys.D)) {
				xsteer += 1f;
			}
			if (Gdx.input.isKeyPressed(Keys.A)) {
				xsteer -= 1f;
			}

			movement.apply(body, speed, 3f, xsteer, ysteer);

			for (SpriteAnimation anim : walk) {
				anim.tick(movement.isMoving() ? 0.016f : 0f);
			}
			VirtualThread.sleep(16);
		}
	}

	/* (non-Javadoc)
	 * @see org.matheusdev.ddm.entity.Entity#draw(org.matheusdev.ddm.entity.EntityHandler, com.badlogic.gdx.graphics.g2d.SpriteBatch)
	 */
	@Override
	public void draw(EntityManager manager, SpriteBatch batch) {
		if (movement.isMoving()) {
			sprite.setRegion(walk[movement.getDirection()].getCurrentKeyframe());
		} else {
			sprite.setRegion(stand[movement.getDirection()]);
		}
		draw(sprite, body, 1f, 0f, 0.2f, batch);
	}

	@Override
	public String toString() {
		return "Player Entity at " + body.getPosition();
	}
}
