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

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;


/**
 * @author matheusdev
 *
 */
public class EntityBall extends Entity {
	private static final long serialVersionUID = -3302177956835701064L;

	private final Sprite sprite;

	/**
	 * @param body
	 * @param sprites
	 */
	public EntityBall(float x, float y, EntityManager entityManager) {
		super(createCircularBody(x, y, 0.15f, 0.7f, 0.4f, 0.5f, entityManager.getPhysics()));
		uploadAsUserData(body);
		sprite = new Sprite(entityManager.getResources().getRegion("ball"));
	}

	@Override
	public void collide(Fixture other, Contact contact, Manifold manifold) {
	}

	@Override
	public void run() {
	}

	@Override
	public void draw(EntityManager manager, SpriteBatch batch) {
		draw(sprite, body, 0.3f, batch);
	}

	@Override
	public String toString() {
		return "Ball Entity at " + body.getPosition();
	}

}
