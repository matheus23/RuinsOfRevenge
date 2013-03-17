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
package org.matheusdev.ror.net.packages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Vector2;
import org.matheusdev.util.Config;
import org.matheusdev.util.FloatUtils;

/**
 * @author matheusdev
 *
 */
public class Input extends NetPackage {

	private static final Vector2 steer = new Vector2();

	public float steerx, steery;

	public Input() {
	}

	public Input(long time, Controller gamepad) {
		set(time, gamepad);
	}

	public Input(long time, int connectionID, float steerx, float steery) {
		super(time);
		this.steerx = steerx;
		this.steery = steery;
	}

	public void set(long time, Controller gamepad) {
		steer.set(0, 0);

		if (Gdx.input.isKeyPressed(Config.get().key("up"))) steer.y += 1f;
		if (Gdx.input.isKeyPressed(Config.get().key("down"))) steer.y -= 1f;
		if (Gdx.input.isKeyPressed(Config.get().key("left"))) steer.x -= 1f;
		if (Gdx.input.isKeyPressed(Config.get().key("right"))) steer.x += 1f;

		if (Config.get().enableGamepad && gamepad != null) {
			float xGamepad = gamepad.getAxis(Config.get().gamepadX);
			float yGamepad = gamepad.getAxis(Config.get().gamepadY);

			if (FloatUtils.equalsEpsilon(xGamepad, 0f, 0.1f)) {
				xGamepad = 0f;
			}
			if (FloatUtils.equalsEpsilon(yGamepad, 0f, 0.1f)) {
				yGamepad = 0f;
			}

			if (!FloatUtils.equalsEpsilon(xGamepad, 0, 0.1f)
					|| !FloatUtils.equalsEpsilon(yGamepad, 0, 0.1f)) {
				steer.x += xGamepad;
				steer.y += -yGamepad;
			}
		}

		float len = steer.len();
		len = Math.min(1f, len);
		steer.nor().scl(len);

		steerx = steer.x;
		steery = steer.y;
	}

	public void set(long time, Input other) {
		steerx = other.steerx;
		steery = other.steery;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (!(other.getClass() == getClass()))
			return false;
		else {
			Input in = (Input) other;
			if (!FloatUtils.equalsEpsilon(steerx, in.steerx, 0.1f)) return false;
			if (!FloatUtils.equalsEpsilon(steery, in.steery, 0.1f)) return false;
			return true;
		}
	}

	@Override
	public String toString() {
		return String.format(
				"[Input]:\n" +
				"\tsteerx: %G\n" +
				"\tsteery: %G",
				steerx, steery);
	}

}
