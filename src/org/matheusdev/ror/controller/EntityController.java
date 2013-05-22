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

import net.indiespot.continuations.VirtualRunnable;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.ror.net.packages.EntityState;
import org.matheusdev.ror.net.packages.Input;

/**
 * @author matheusdev
 *
 */
public abstract class EntityController implements VirtualRunnable {
	private static final long serialVersionUID = -1834760944554078514L;

	protected final Entity entity;
	protected Input input;
	protected EntityState state;
	protected boolean living = true;

	public EntityController(Entity entity) {
		this.entity = entity;
	}

	public void setInput(Input in) {
		this.input = in;
	}

	public void setEntityState(EntityState state) {
		this.state = state;
	}

	public void kill() {
		living = false;
	}

	public Entity getEntity() {
		return entity;
	}

}
