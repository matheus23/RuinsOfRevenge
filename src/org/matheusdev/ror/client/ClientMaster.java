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

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import org.matheusdev.ror.EntityParser;
import org.matheusdev.ror.Master;
import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.collision.Physics;
import org.matheusdev.ror.controller.ControllerPlayer;
import org.matheusdev.ror.controller.EntityController;
import org.matheusdev.ror.controller.EntityControllers;
import org.matheusdev.ror.map.FringeLayer;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.ror.net.packages.CreateEntity;
import org.matheusdev.ror.net.packages.EntityState;
import org.matheusdev.ror.net.packages.FetchEntities;
import org.matheusdev.ror.net.packages.Input;
import org.matheusdev.ror.view.EntityView;
import org.matheusdev.util.Config;
import org.matheusdev.util.JsonDOM.JsonObject;
import org.matheusdev.util.PingPongEq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author matheusdev
 *
 */
public class ClientMaster extends Master {

	private boolean disposed;

	private final EntityControllers controllers = new EntityControllers();
	private final List<ClientEntity> entities = new ArrayList<>();
	private final ObjectMap<Integer, ClientEntity> entitiesById = new ObjectMap<>();

	private final ResourceLoader res;
	private final Physics physics;
	private final ClientConnector connection;
	private final PingPongEq<Input> inputs;
	private Controller gamepad;

	private long time;
	private ClientEntity player;
	private ControllerPlayer playerContr;

	public ClientMaster(ResourceLoader res, String basePath, String ip) throws IOException {
		super(basePath);
		this.res = res;
		this.physics = new Physics(new Vector2(0, 0), true);
		this.connection = createConnection(ip);
		this.inputs = new PingPongEq<>(new Input(), new Input());
		this.gamepad = getController(Config.get().gamepad);
	}

	public void initializeEntities(Vector2 spawn) {
		connection.send(new FetchEntities());
		EntityState state = new EntityState();
		state.posX = spawn.x;
		state.posY = spawn.y;
		state.belongsTo = connection.getClient().getID();
		createEntity("player", state);
	}

	public boolean hasInitialized() {
		return player != null;
	}

	public void updateEntity(EntityState state) {
        entitiesById.get(state.id).getController().setEntityState(state);
	}

	private Controller getController(String name) {
		for (Controller cont : Controllers.getControllers()) {
			if (cont.getName().equals(name)) {
				return cont;
			}
		}
		System.err.println("Couldn't find controller for name \"" + name + "\"");
		return null;
	}

	public ClientConnector createConnection(String host) throws IOException {
		return new ClientConnector(this, host);
	}

	public Physics getPhysics() {
		return physics;
	}

	public void tick(float delta) {
		long msDelta = (long)(delta * 1000f);
		time += msDelta;
		inputs.get().set(time, gamepad);
		if (inputs.needUpdate()) {
			connection.send(inputs.get());
			inputs.swap();
		}
		if (playerContr != null)
			playerContr.setInput(connection.getNewestInput());

		Collections.sort(entities);
		controllers.tick(msDelta);
		connection.tick(msDelta);
		physics.step(delta);
	}

	public void draw(SpriteBatch batch, FringeLayer layer, float delta) {
		layer.begin();
		for (ClientEntity e : entities) {
			layer.renderTill(batch, e.getEntity().getY());
			e.getView().draw(batch, e.getEntity(), delta);
		}
		layer.end(batch);
	}

	public void dispose() {
		if (!disposed) {
			disposed = true;
			physics.dispose();
			connection.dispose();
		}
	}

	public ClientEntity setPlayer(ClientEntity e) {
		if (!(e.getController() instanceof ControllerPlayer)) {
			throw new IllegalArgumentException("Player entity needs to be under control by the " + ControllerPlayer.name);
		}
		playerContr = (ControllerPlayer) e.getController();
		return player = e;
	}

	public ClientEntity getPlayer() {
		return player;
	}

	public void createEntity(String type, EntityState e) {
		connection.send(new CreateEntity(time, type, e));
	}

	public ClientEntity addEntity(String type, EntityState state) {
		JsonObject json = getEntityJson(type);
		Entity e = EntityParser.createEntity(physics, type, json, state.id, connection.getClient().getID());
		EntityController contr = EntityParser.createController(controllers, e, json);
		EntityView view = EntityParser.createView(res, json);

		ClientEntity entity = new ClientEntity(e, contr, view);
		state.setFromState(entity.getEntity());
		entities.add(entity);
		entitiesById.put(state.id, entity);
		return entity;
	}

	public ClientEntity addEntity(CreateEntity create) {
		ClientEntity e = addEntity(create.type, create.state);
		if (create.state.belongsTo == connection.getClient().getID()) {
			setPlayer(e);
		}
		return e;
	}

	public void removeEntity(ClientEntity e) {
		entities.remove(e);
		entitiesById.remove(entitiesById.findKey(e, true));
        physics.getWorld().destroyBody(e.getEntity().getBody());
	}

	public void removeEntity(int entityID) {
		ClientEntity e = entitiesById.get(entityID);
		removeEntity(e);
	}

}
