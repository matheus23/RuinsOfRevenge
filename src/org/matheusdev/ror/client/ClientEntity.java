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
package org.matheusdev.ror.client;

import org.matheusdev.ror.controller.EntityController;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.ror.view.EntityView;

/**
 * @author matheusdev
 *
 */
public class ClientEntity implements Comparable<ClientEntity> {

	protected final EntityController controller;
	protected final Entity entity;
	protected final EntityView view;

	public ClientEntity(Entity entity, EntityController controller, EntityView view) {
		this.entity = entity;
		this.controller = controller;
		this.view = view;
	}

	public EntityController getController() {
		return controller;
	}

	public EntityView getView() {
		return view;
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public int compareTo(ClientEntity e) {
		return entity.compareTo(e.getEntity());
	}

}
