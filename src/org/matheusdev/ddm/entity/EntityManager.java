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
package org.matheusdev.ddm.entity;

import org.matheusdev.ddm.ResourceLoader;
import org.matheusdev.ddm.collision.Physics;
import org.matheusdev.util.ReadWriteCollection;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * @author matheusdev
 *
 */
public class EntityManager {

	private final ReadWriteCollection<Entity> entities;
	private final Physics physics;
	private final ResourceLoader res;

	public EntityManager(Physics physics, ResourceLoader res) {
		this.entities = new ReadWriteCollection<>();
		this.physics = physics;
		this.res = res;
	}

	public void tick(float delta) {
		for (Entity e : entities) {
			e.tick(this, delta);
			if (e.isDead()) {
				entities.remove();
			}
		}
	}

	public void draw(SpriteBatch batch) {
		for (Entity e : entities) {
			e.draw(this, batch);
		}
	}

	public void add(Entity e) {
		entities.add(e);
	}

	public Physics getPhysics() {
		return physics;
	}

	public ResourceLoader getResources() {
		return res;
	}

	public ReadWriteCollection<Entity> getEntityList() {
		return entities;
	}

}
