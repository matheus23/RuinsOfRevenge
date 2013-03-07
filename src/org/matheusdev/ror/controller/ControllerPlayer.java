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
package org.matheusdev.ror.controller;

import net.indiespot.continuations.VirtualProcessor;
import net.indiespot.continuations.VirtualThread;

import org.matheusdev.ror.controller.component.ComponentMovement;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.util.Config;
import org.matheusdev.util.Dir;
import org.matheusdev.util.FloatUtils;
import org.matheusdev.util.JsonDOM.JsonObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;

import de.matthiasmann.continuations.SuspendExecution;

/**
 * @author matheusdev
 *
 */
public class ControllerPlayer extends EntityController {
	private static final long serialVersionUID = 8212372970195205197L;

	public static final String name = "ControllerPlayer";

	private final ComponentMovement movement = new ComponentMovement(Dir.DOWN);
	private final float strength;
	private final float maxspeed;
	private final float friction;

	public ControllerPlayer(VirtualProcessor proc, Entity entity, JsonObject config) {
		super(proc, entity);
		strength = Float.parseFloat(config.values.get("strength"));
		maxspeed = Float.parseFloat(config.values.get("maxspeed"));
		friction = Float.parseFloat(config.values.get("friction"));
	}

	private Controller getController(String name) {
		for (Controller cont : Controllers.getControllers()) {
			if (cont.getName().equals(name)) {
				return cont;
			}
		}
		System.err.println("Couldn't find controllers!");
		return null;
	}

	@Override
	public void run() throws SuspendExecution {
		movement.set(strength, maxspeed, friction);
		Vector2 steer = new Vector2();
		Controller gamepad = getController(Config.get().gamepad);
		while (true) {
			long time = VirtualThread.currentThread().getProcessor().getCurrentTime();
			steer.set(0, 0);

			if (Gdx.input.isKeyPressed(Config.get().key("up")))
				steer.y += 1f;
			if (Gdx.input.isKeyPressed(Config.get().key("down")))
				steer.y -= 1f;
			if (Gdx.input.isKeyPressed(Config.get().key("left")))
				steer.x -= 1f;
			if (Gdx.input.isKeyPressed(Config.get().key("right")))
				steer.x += 1f;

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
			steer.nor().mul(len);

			movement.setSteer(steer.x, steer.y);

			movement.apply(entity);
			VirtualThread.wakeupAt(time + 16);
		}
	}

	public ComponentMovement getMovementComponent() {
		return movement;
	}

}
