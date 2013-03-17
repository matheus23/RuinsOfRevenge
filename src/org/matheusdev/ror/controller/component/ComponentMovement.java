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
package org.matheusdev.ror.controller.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.util.Dir;

/**
 * @author matheusdev
 *
 */
public class ComponentMovement extends Component {

	private boolean moving;
	private int direction;

	private float strength;
	private float maxspeed;
	private float friction;
	private float xsteer;
	private float ysteer;

	public ComponentMovement(int startDir) {
		this.direction = startDir;
	}

	public void set(float strength, float maxspeed, float friction, float xsteer, float ysteer) {
		this.strength = strength;
		this.maxspeed = maxspeed;
		this.friction = friction;
		this.xsteer = xsteer;
		this.ysteer = ysteer;
	}

	public void set(float strength, float maxspeed, float friction) {
		this.strength = strength;
		this.maxspeed = maxspeed;
		this.friction = friction;
	}

	public void setSteer(float xsteer, float ysteer) {
		this.xsteer = xsteer;
		this.ysteer = ysteer;
	}

	public void setSteer(float strength, float xsteer, float ysteer) {
		this.strength = strength;
		this.xsteer = xsteer;
		this.ysteer = ysteer;
	}

	public void setMaxspeed(float maxspeed) {
		this.maxspeed = maxspeed;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public void setFriction(float friction) {
		this.friction = friction;
	}

	@Override
	public void apply(Entity entity) {
		Body body = entity.getBody();
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
		if (linVel.len() > maxspeed) {
			body.setLinearVelocity(linVel.cpy().nor().mul(maxspeed));
		}

		body.applyForceToCenter(strength * xsteer, strength * ysteer);

		if (friction > 1f && !moving) {
			body.setLinearVelocity(linVel.div(friction));
		}
	}

	public boolean isMoving() {
		return moving;
	}

	public int getDirection() {
		return direction;
	}

}
