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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.XmlReader;
import org.matheusdev.ror.EntityParser;
import org.matheusdev.ror.Master;
import org.matheusdev.ror.RuinsOfRevenge;
import org.matheusdev.ror.collision.Physics;
import org.matheusdev.ror.controller.EntityController;
import org.matheusdev.ror.controller.EntityControllers;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.ror.net.packages.DeleteEntity;
import org.matheusdev.ror.net.packages.EntityState;
import org.matheusdev.util.FileLocation;
import org.matheusdev.util.JsonDOM.JsonObject;
import org.matheusdev.util.TmxObjectsLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author matheusdev
 *
 */
public class ServerMaster extends Master {

	public static void main(String[] args) throws IOException, InterruptedException {
		ServerMaster server = new ServerMaster("data/maps/newmap/map004.tmx", "data/entities/");
		long startTime = System.currentTimeMillis();
		while (true) {
			long then = System.currentTimeMillis();

			server.tick(System.currentTimeMillis()-startTime);

			long now = System.currentTimeMillis();
			long sleeptime = 16 - (now-then);
			if (sleeptime > 0)
				Thread.sleep(sleeptime);
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
	private long time;

	public ServerMaster(String mapfile, String basePath) throws IOException {
		super(basePath);
		this.physics = new Physics(new Vector2(0, 0), true);
		if (RuinsOfRevenge.fileLocation == FileLocation.CLASSPATH)
			this.mapObjects = new TmxObjectsLoader(new XmlReader().parse(
					Thread.currentThread().getContextClassLoader().getResourceAsStream(mapfile)));
		else
			this.mapObjects = new TmxObjectsLoader(new XmlReader().parse(new FileHandle(mapfile)));
		this.connection = new ServerConnection(this, PORT);
		this.controllers = new EntityControllers();

		for (TmxObjectsLoader.TmxObjectGroup group : mapObjects.getObjectGroups()) {
			for (TmxObjectsLoader.TmxObject obj : group.objects) {
				if (!obj.name.equalsIgnoreCase("spawnpoint")) {
					mapObjects.loadToPhysics(obj, physics);
				} else {
					System.out.println("spawnpoint found: " + obj.name);
				}
			}
		}
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
		this.time = time;
		connection.tick();

		for (ServerEntity e : entities) {
			e.tick(connection.getInput(e.getEntity().getBelongsTo()));
		}

		controllers.tick(time);
		physics.step(0.016f);

		for (ServerEntity e : entities) {
			connection.send(new EntityState(time, e.getEntity()));
		}
	}

	public ServerEntity addEntity(String type, EntityState state, int connectionID) {
		JsonObject json = getEntityJson(type);
		Entity e = EntityParser.createEntity(physics, type, json, state.id, connectionID);
		EntityController contr = EntityParser.createController(controllers, e, json);

		state.setFromState(e);
		ServerEntity entity = new ServerEntity(e, contr);
		entities.add(entity);
		return entity;
	}

	public void removeEntity(ServerEntity e) {
		entities.remove(e);
		e.getController().kill();
		destroyEntityBody(e);
	}

	public void removeEntities(int connectionID) {
		Iterator<ServerEntity> itr = entities.iterator();
		while (itr.hasNext()) {
			ServerEntity e = itr.next();
			if (e.getEntity().getBelongsTo() == connectionID) {
				itr.remove();
				destroyEntityBody(e);
				e.getController().kill();
				connection.send(new DeleteEntity(time, e.getEntity().getID()));
			}
		}
	}

	private void destroyEntityBody(ServerEntity e) {
		physics.getWorld().destroyBody(e.getEntity().getBody());
	}

	public long getTime() {
		return time;
	}

}
