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
package org.matheusdev.ror;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.matheusdev.ror.collision.Physics;
import org.matheusdev.ror.controller.EntityController;
import org.matheusdev.ror.controller.EntityControllers;
import org.matheusdev.ror.map.FringeLayer;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.ror.view.EntityView;
import org.matheusdev.ror.view.EntityViews;
import org.matheusdev.util.JsonDOM;
import org.matheusdev.util.JsonDOM.JsonArray;
import org.matheusdev.util.JsonDOM.JsonElement;
import org.matheusdev.util.JsonDOM.JsonObject;
import org.matheusdev.util.MissingJSONContentException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author matheusdev
 *
 */
public class ClientMaster {

	private boolean disposed;

	private final EntityControllers controllers = new EntityControllers();
	private final EntityViews views = new EntityViews();
	private final List<ClientEntity> entities = new ArrayList<>();

	private final ObjectMap<String, JsonObject> entityTypePool = new ObjectMap<>();
	private final ResourceLoader res;
	private final String basePath;
	private final Physics physics;

	public ClientMaster(ResourceLoader res, String basePath) {
		this.res = res;
		this.basePath = basePath;
		this.physics = new Physics(new Vector2(0, 0), true);
	}

	public Physics getPhysics() {
		return physics;
	}

	public void tick(float delta) {
		Collections.sort(entities);
		controllers.tick((long)(delta * 1000f));
		physics.step(delta);
	}

	public void draw(SpriteBatch batch, FringeLayer layer, float delta) {
		layer.begin();
		for (ClientEntity e : entities) {
			layer.renderTill(batch, e.getEntity().getY());
			e.getView().draw(batch, e, delta);
		}
		layer.end(batch);
	}

	public void dispose() {
		if (!disposed) {
			disposed = true;
			physics.dispose();
		}
	}

	public ClientEntity addEntity(String type) {
		if (entityTypePool.containsKey(type)) {
			ClientEntity e = createEntity(entityTypePool.get(type));
			entities.add(e);
			return e;
		} else {
			try {
				JsonDOM dom = new Json().fromJson(JsonDOM.class, Gdx.files.internal(basePath + type + ".json"));
				entityTypePool.put(type, dom.getRoot());
				ClientEntity e = createEntity(dom.getRoot());
				entities.add(e);
				return e;
			} catch (Exception e) {
				System.err.println(new UnkownEntityTypeException("Couldn't load entity named \"" + type + "\" from " + basePath + type + ".json: " + e));
				throw e;
			}
		}
	}

	public ClientEntity createEntity(JsonObject json) {
		JsonObject jsonBodyObject = null;
		if (json.elements.get("body") instanceof JsonObject) {
			jsonBodyObject = (JsonObject) json.elements.get("body");
		} else {
			throw new MissingJSONContentException("Missing \"body: { ... }\" tag in entity");
		}
		JsonArray jsonFixturesList = null;
		if (json.elements.get("fixtures") instanceof JsonArray) {
			jsonFixturesList = (JsonArray) json.elements.get("fixtures");
		} else {
			throw new MissingJSONContentException("Missing \"fixtures: [ ... ]\" tag in entity");
		}
		JsonObject jsonControllerConf = null;
		if (json.elements.get("controller-conf") instanceof JsonObject) {
			jsonControllerConf = (JsonObject) json.elements.get("controller-conf");
		} else {
			throw new MissingJSONContentException("Missing \"controller-conf: { ... }\" tag in entity");
		}
		JsonObject jsonViewConf = null;
		if (json.elements.get("view-conf") instanceof JsonObject) {
			jsonViewConf = (JsonObject) json.elements.get("view-conf");
		} else {
			throw new MissingJSONContentException("Missing \"view-conf: { ... }\" tag in entity");
		}

		Body body = parseBody(jsonBodyObject);
		// Attach fixtures to body:
		parseFixtures(body, jsonFixturesList);

		Entity e = new Entity(body, new Sprite(res.getRegion("white")));
		EntityController contr = controllers.createController(json.values.get("controller"), e, jsonControllerConf);
		EntityView view = views.createView(json.values.get("view"), res, jsonViewConf);

		return new ClientEntity(e, contr, view);
	}

	public Body parseBody(JsonObject bodyJson) {
		BodyDef def = new BodyDef();
		def.position.set(parseVector(bodyJson.values.get("position")));
		def.linearVelocity.set(parseVector(bodyJson.values.get("linearVelocity")));
		def.type = 				BodyType.valueOf(bodyJson.values.get("type"));
		def.angle = 			Float.parseFloat(bodyJson.values.get("angle"));
		def.angularVelocity = 	Float.parseFloat(bodyJson.values.get("angularVelocity"));
		def.linearDamping = 	Float.parseFloat(bodyJson.values.get("linearDamping"));
		def.angularDamping = 	Float.parseFloat(bodyJson.values.get("angularDamping"));
		def.allowSleep = 		Boolean.parseBoolean(bodyJson.values.get("allowSleep"));
		def.awake = 			Boolean.parseBoolean(bodyJson.values.get("awake"));
		def.fixedRotation = 	Boolean.parseBoolean(bodyJson.values.get("fixedRotation"));
		def.bullet = 			Boolean.parseBoolean(bodyJson.values.get("bullet"));
		def.active = 			Boolean.parseBoolean(bodyJson.values.get("active"));
		def.gravityScale = 		Float.parseFloat(bodyJson.values.get("gravityScale"));
		return physics.getWorld().createBody(def);
	}

	public void parseFixtures(Body body, JsonArray fixtureArray) {
		for (JsonObject obj : fixtureArray.elements) {
			JsonElement shapeElem = obj.elements.get("shape");
			Shape shape = null;
			if (shapeElem instanceof JsonObject) {
				shape = parseShape((JsonObject) shapeElem);
			} else {
				throw new MissingJSONContentException("Missing \"shape: { ... }\" tag in fixture list of entity");
			}
			FixtureDef fixDef = new FixtureDef();
			fixDef.shape = shape;
			fixDef.friction = Float.parseFloat(obj.values.get("friction"));
			fixDef.restitution = Float.parseFloat(obj.values.get("restitution"));
			fixDef.density = Float.parseFloat(obj.values.get("density"));
			fixDef.isSensor = Boolean.parseBoolean(obj.values.get("isSensor"));
			body.createFixture(fixDef);
		}
	}

	public Shape parseShape(JsonObject shapeDef) {
		// TODO: Add other Shape types:
		Shape finalshape = null;
		switch (shapeDef.values.get("type")) {
		case "circle":
			CircleShape shape = new CircleShape();
			shape.setPosition(parseVector(shapeDef.values.get("position")));
			shape.setRadius(Float.parseFloat(shapeDef.values.get("radius")));
			finalshape = shape;
			break;
		}
		return finalshape;
	}

	public Vector2 parseVector(String str) {
		String[] dims = str.split(",");
		return new Vector2(Float.parseFloat(dims[0]), Float.parseFloat(dims[1]));
	}

}
