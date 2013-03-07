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
package org.matheusdev.ror.model.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * @author matheusdev
 *
 */
public class Entity implements Comparable<Entity> {

	protected final Body body;
	protected final Sprite sprite;

	public Entity(Body body, Sprite sprite) {
		this.body = body;
		this.sprite = sprite;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public Body getBody() {
		return body;
	}

	public Vector2 getPos() {
		return body.getPosition();
	}

	public float getX() {
		return body.getPosition().x;
	}

	public float getY() {
		return body.getPosition().y;
	}

	public float getRotation() {
		return body.getAngle();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Entity e) {
		if (e.getY() > getY()) return 1;
		else if (e.getY() < getY()) return -1;
		else return 0;
	}

}
