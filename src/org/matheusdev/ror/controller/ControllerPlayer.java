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

import net.indiespot.continuations.VirtualThread;

import org.matheusdev.ror.controller.component.ComponentMovement;
import org.matheusdev.ror.controller.component.ComponentNetwork;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.util.Dir;
import org.matheusdev.util.JsonDOM.JsonObject;

import de.matthiasmann.continuations.SuspendExecution;

/**
 * @author matheusdev
 *
 */
public class ControllerPlayer extends EntityController {
	private static final long serialVersionUID = 8212372970195205197L;

	public static final String name = "ControllerPlayer";

	private final ComponentMovement movement = new ComponentMovement(Dir.DOWN);
	private final ComponentNetwork network = new ComponentNetwork();
	private final float strength;
	private final float maxspeed;
	private final float friction;

	public ControllerPlayer(Entity entity, JsonObject config) {
		super(entity);
		strength = Float.parseFloat(config.values.get("strength"));
		maxspeed = Float.parseFloat(config.values.get("maxspeed"));
		friction = Float.parseFloat(config.values.get("friction"));
	}

	@Override
	public void run() throws SuspendExecution {
		movement.set(strength, maxspeed, friction);
		while (true) {
			long time = VirtualThread.currentThread().getProcessor().getCurrentTime();

			if (state != null) {
				System.out.println("Updating position from server");
				network.setRemoteState(state);
				network.apply(entity);
			}

			movement.setSteer(input.steerx, input.steery);
			movement.apply(entity);

			state = null;

			VirtualThread.wakeupAt(time + 16);
		}
	}

	public ComponentMovement getMovementComponent() {
		return movement;
	}

}
