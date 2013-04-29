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
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.ror.net.packages.EntityState;

/**
 * @author matheusdev
 *
 */
public class ComponentNetwork extends Component {

	private final Vector2 posDiffPool = new Vector2();

	private EntityState remoteState;

	public void setRemoteState(EntityState state) {
		this.remoteState = state;
	}

	@Override
	public void apply(Entity entity) {
		Vector2 entityPos = entity.getPos();
		Vector2 posDiff = posDiffPool.set(
				remoteState.posX - entityPos.x,
				remoteState.posY - entityPos.y);
		float distance = posDiff.len();

		if (distance > 0.5f) {
			entity.getBody().setTransform(remoteState.posX, remoteState.posY, remoteState.angle);
			System.out.println("Set position to: " + remoteState.posX + ", " + remoteState.posY);
		} else if (distance > 0.1f) {
			entity.getBody().setTransform(entityPos.add(posDiff.scl(0.1f)), remoteState.angle);
		}

		entity.getBody().setLinearVelocity(remoteState.velX, remoteState.velY);
	}

}
