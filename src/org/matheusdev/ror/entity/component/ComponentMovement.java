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
package org.matheusdev.ror.entity.component;

import org.matheusdev.util.Dir;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * @author matheusdev
 *
 */
public class ComponentMovement {

	private boolean moving;
	private int direction;

	public ComponentMovement(int startDir) {
		this.direction = startDir;
	}

	public void apply(Body body, float strength, float maxspeed, float xsteer, float ysteer) {
		moving = false;

		// If trying to move (pressing buttons on Keyboard, steering with Gamepad)
		if (xsteer != 0f || ysteer != 0f) {
			moving = true;

			if (Math.abs(xsteer) > Math.abs(ysteer)) {
				if (xsteer < 0) {
					direction = Dir.LEFT;
				} else {
					direction = Dir.RIGHT;
				}
			} else {
				if (ysteer < 0) {
					direction = Dir.DOWN;
				} else {
					direction = Dir.UP;
				}
			}
		}

		Vector2 linVel = body.getLinearVelocity();
		if (linVel.len() > 3f) {
			body.setLinearVelocity(linVel.cpy().nor().mul(3f));
		}

		body.applyForceToCenter(strength * xsteer, strength * ysteer);

		if (!moving) {
			body.setLinearVelocity(linVel.div(1.5f));
		}
	}

	public boolean isMoving() {
		return moving;
	}

	public int getDirection() {
		return direction;
	}

}
