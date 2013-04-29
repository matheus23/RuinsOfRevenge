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

import com.badlogic.gdx.math.Vector2;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.util.FloatUtils;

/**
 * @author matheusdev
 *
 */
public class EntityState extends NetPackage {

	public float posX;
	public float posY;
	public float angle;
	public float velX;
	public float velY;
	public float rotVel;
	public int id;
	public int belongsTo;

	public EntityState() {
	}

	public EntityState(long time, Entity e) {
		this(time, e.getID(), e.getBelongsTo(),
				e.getBody().getPosition(),
				e.getBody().getAngle(),
				e.getBody().getLinearVelocity(),
				e.getBody().getAngularVelocity());
	}

	public EntityState(long time, int id, int belongsTo, float posX, float posY, float angle, float velX, float velY, float rotVel) {
		super(time);
		this.id = id;
		this.posX = posX;
		this.posY = posY;
		this.angle = angle;
		this.velX = velX;
		this.velY = velY;
		this.rotVel = rotVel;
		this.belongsTo = belongsTo;
	}

	public EntityState(long time, int id, int belongsTo, Vector2 pos, float angle, Vector2 vel, float rotVel) {
		this(time, id, belongsTo, pos.x, pos.y, angle, vel.x, vel.y, rotVel);
	}

	public void set(long time, int id, int belongsTo, float posX, float posY, float angle, float velX, float velY, float rotVel) {
		this.time = time;
		this.id = id;
		this.posX = posX;
		this.posY = posY;
		this.angle = angle;
		this.velX = velX;
		this.velY = velY;
		this.rotVel = rotVel;
		this.belongsTo = belongsTo;
	}

	public void set(long time, int id, int belongsTo, Vector2 pos, float angle, Vector2 vel, float rotVel) {
		set(time, id, belongsTo, pos.x, pos.y, angle, vel.x, vel.y, rotVel);
	}

	@Override
	public String toString() {
		return String.format(
				"[EntityState]:\n" +
				"\tid: %d\n" +
				"\tbelongsTo: %d\n" +
				"\tposition: [%G, %G]\n" +
				"\tangle: %G\n" +
				"\tvelocity: [%G, %G]\n" +
				"\trot-velocity: %G\n",
				id, belongsTo, posX, posY, angle, velX, velY, rotVel);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (!(other.getClass() == getClass())) {
			return false;
		} else {
			EntityState state = (EntityState) other;
			if (!FloatUtils.equalsEpsilon(posX, state.posX, 0.01f)) return false;
			if (!FloatUtils.equalsEpsilon(posY, state.posY, 0.01f)) return false;
			if (!FloatUtils.equalsEpsilon(angle, state.angle, 0.01f)) return false;
			if (!FloatUtils.equalsEpsilon(velX, state.velX, 0.01f)) return false;
			if (!FloatUtils.equalsEpsilon(velY, state.velY, 0.01f)) return false;
			if (!FloatUtils.equalsEpsilon(rotVel, state.rotVel, 0.01f)) return false;
			return true;
		}
	}

	public EntityState setFromState(Entity e) {
		e.getBody().setTransform(posX, posY, angle);
		e.getBody().setLinearVelocity(velX, velY);
		return this;
	}

}
