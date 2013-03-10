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

import org.matheusdev.ror.collision.Physics;
import org.matheusdev.ror.controller.EntityController;
import org.matheusdev.ror.controller.EntityControllers;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.ror.view.EntityView;
import org.matheusdev.ror.view.EntityViews;
import org.matheusdev.util.JsonDOM.JsonArray;
import org.matheusdev.util.JsonDOM.JsonElement;
import org.matheusdev.util.JsonDOM.JsonObject;
import org.matheusdev.util.MissingJSONContentException;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * @author matheusdev
 *
 */
public final class EntityParser {

	private EntityParser() {
	}

	private static JsonObject getJsonObject(JsonObject json, String name) {
		JsonObject jsonObject = null;
		if (json.elements.get(name) instanceof JsonObject) {
			jsonObject = (JsonObject) json.elements.get(name);
		} else {
			throw new MissingJSONContentException("Missing \"" + name + ": { ... }\" tag in entity");
		}
		return jsonObject;
	}

	private static JsonArray getJsonArray(JsonObject json, String name) {
		JsonArray jsonArray = null;
		if (json.elements.get(name) instanceof JsonArray) {
			jsonArray = (JsonArray) json.elements.get(name);
		} else {
			throw new MissingJSONContentException("Missing \"" + name + ": [ ... ]\" tag in entity");
		}
		return jsonArray;
	}

	public static EntityController createController(EntityControllers controllers, Entity e, JsonObject json) {
		JsonObject jsonControllerConf = getJsonObject(json, "controller-conf");

		return controllers.createController(json.values.get("controller"), e, jsonControllerConf);
	}

	public static EntityView createView(EntityViews views, ResourceLoader res, JsonObject json) {
		JsonObject jsonViewConf = getJsonObject(json, "view-conf");
		return views.createView(json.values.get("view"), res, jsonViewConf);
	}

	public static Entity createEntity(Physics physics, String type, JsonObject json, Sprite sprite, int id, int belongsTo) {
		JsonObject jsonBodyObject = getJsonObject(json, "body");
		JsonArray jsonFixturesList = getJsonArray(json, "fixtures");

		Body body = createBody(physics, jsonBodyObject);
		// Attach fixtures to body:
		parseFixtures(body, jsonFixturesList);

		return new Entity(body, sprite, type, id, belongsTo);
	}

	public static Body createBody(Physics physics, JsonObject bodyJson) {
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

	public static void parseFixtures(Body body, JsonArray fixtureArray) {
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

	public static Shape parseShape(JsonObject shapeDef) {
		Shape finalshape = null;
		switch (shapeDef.values.get("type")) {
		case "circle":
			CircleShape circleShape = new CircleShape();
			circleShape.setPosition(parseVector(shapeDef.values.get("position")));
			circleShape.setRadius(Float.parseFloat(shapeDef.values.get("radius")));
			finalshape = circleShape;
			break;
		case "rectangle":
		case "rect":
			PolygonShape rectShape = new PolygonShape();
			Vector2 pos = parseVector(shapeDef.values.get("position"));
			Vector2 size = parseVector(shapeDef.values.get("size"));
			rectShape.setAsBox(size.x, size.y);
			Vector2[] rectVertices = new Vector2[rectShape.getVertexCount()];
			for (int i = 0; i < rectShape.getVertexCount(); i++) {
				rectVertices[i] = new Vector2();
				rectShape.getVertex(i, rectVertices[i]);
				rectVertices[i].add(pos);
			}
			rectShape.set(rectVertices);
			finalshape = rectShape;
			break;
		case "polygon":
		case "poly":
			PolygonShape polyShape = new PolygonShape();
			if (!(shapeDef.elements.get("vertices") instanceof JsonArray))
				throw new MissingJSONContentException("Missing \"vertices: [ { v: ... }, ... ]\" array");
			JsonArray polyVertArray = (JsonArray) shapeDef.elements.get("vertices");
			Vector2[] polyVertices = new Vector2[polyVertArray.elements.size()];
			for (int i = 0; i < polyVertArray.elements.size(); i++) {
				JsonObject o = polyVertArray.elements.get(i);
				if (!o.values.containsKey("v"))
					throw new MissingJSONContentException("Need vertex data in Polygon definition: \"vertices: [ { v: \"0,0\" }, ... ]\"");
				polyVertices[i] = parseVector(o.values.get("v"));
			}
			polyShape.set(polyVertices);
			finalshape = polyShape;
			break;
		}
		return finalshape;
	}

	public static Vector2 parseVector(String str) {
		String[] dims = str.split(",");
		return new Vector2(Float.parseFloat(dims[0]), Float.parseFloat(dims[1]));
	}

}
