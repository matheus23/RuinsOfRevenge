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
package org.matheusdev.ror.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.matheusdev.ror.EntityParser;
import org.matheusdev.ror.Master;
import org.matheusdev.ror.collision.Physics;
import org.matheusdev.ror.controller.EntityController;
import org.matheusdev.ror.controller.EntityControllers;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.ror.net.packages.DeleteEntity;
import org.matheusdev.ror.net.packages.EntityState;
import org.matheusdev.util.JsonDOM.JsonObject;
import org.matheusdev.util.TmxObjectsLoader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.XmlReader;

/**
 * @author matheusdev
 *
 */
public class ServerMaster extends Master {

	public static void main(String[] args) throws IOException, InterruptedException {
		ServerMaster server = new ServerMaster("data/maps/newmap/map004.tmx", "data/entities/");
		long time = 0;
		while (true) {
			server.tick(time += 16);
			Thread.sleep(15);
		}
	}

	static {
		GdxNativesLoader.load();
	}

	public static final int PORT = 5455;

	private final List<ServerEntity> entities = new ArrayList<>();
	private final Physics physics;
	private final TmxObjectsLoader mapObjects;
	private final ServerConnection connection;
	private final EntityControllers controllers;

	private int id;

	public ServerMaster(String mapfile, String basePath) throws IOException {
		super(basePath);
		this.physics = new Physics(new Vector2(0, 0), true);
		this.mapObjects = new TmxObjectsLoader(new XmlReader().parse(new FileHandle(mapfile)));
		this.connection = new ServerConnection(this, PORT);
		this.controllers = new EntityControllers();

		mapObjects.loadToPhysics(physics);
	}

	public ServerEntity[] getEntities() {
		ServerEntity[] entityArray = new ServerEntity[entities.size()];
		for (int i = 0; i < entityArray.length; i++) {
			entityArray[i] = entities.get(i);
		}
		return entityArray;
	}

	public int getNewEntityID() {
		id++;
		return id;
	}

	public void tick(long time) {
		connection.tick();

		for (ServerEntity e : entities) {
			e.tick(connection.getInput(e.getConnectionID()));
		}

		controllers.tick(time);
		physics.step(0.016f);

		for (ServerEntity e : entities) {
			connection.send(e.getEntity().getState(time));
		}
	}

	public ServerEntity addEntity(String type, EntityState state, int connectionID) {
		JsonObject json = getEntityJson(type);
		Entity e = EntityParser.createEntity(physics, type, json, null, state.id, -1);
		EntityController contr = EntityParser.createController(controllers, e, json);

		e.setFromState(state);
		ServerEntity entity = new ServerEntity(e, contr, connectionID);
		entities.add(entity);
		return entity;
	}

	public void removeEntity(ServerEntity e) {
		entities.remove(e);
	}

	public void removeEntities(int connectionID) {
		Iterator<ServerEntity> itr = entities.iterator();
		while (itr.hasNext()) {
			ServerEntity e = itr.next();
			if (e.getEntity().getBelongsTo() == connectionID) {
				itr.remove();
				// TODO: fix "time".
				connection.send(new DeleteEntity(0, e.getEntity().getID()));
			}
		}
	}

}
