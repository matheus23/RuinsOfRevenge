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
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.util.JsonDOM.JsonObject;

import java.util.ArrayList;
import java.util.List;


/**
 * @author matheusdev
 *
 */
public class EntityControllers {

	private final VirtualProcessor proc;
	private final List<EntityController> controllers = new ArrayList<>();

	private long stateTime;

	public EntityControllers() {
		proc = new VirtualProcessor();
	}

	public void tick(long msTime) {
		stateTime += msTime;
		proc.tick(stateTime);
	}

	public EntityController createController(String name, Entity e, JsonObject conf) {
		EntityController contr = null;
		switch(name) {
		case ControllerPlayer.name:
			contr = new ControllerPlayer(e, conf);
			break;
		default: throw new UnkownControllerException("Unkown controller: " + name);
		}
		controllers.add(contr);
		new VirtualThread(contr).start(proc);
		return contr;
	}

}
