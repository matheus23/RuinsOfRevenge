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
package org.matheusdev.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import org.matheusdev.ror.collision.Physics;
import org.matheusdev.util.TmxObjectsLoader.TmxObject.Type;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author matheusdev
 *
 */
public class TmxObjectsLoader {

	public static class TmxObject {
		enum Type {
			POLYLINE,
			POLYGON,
			RECTANGLE,
			ELLIPSE
		}
		public final String name;
		public final String type;
		public final String pointData;
		public final Type objectType;
		public final float x, y, width, height;

		public TmxObject(String name, String type, String pointData, Type objectType, float x, float y, float width, float height) {
			this.name = name;
			this.type = type;
			this.pointData = pointData;
			this.objectType = objectType;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}

	public static class TmxObjectGroup {
		public final List<TmxObject> objects;

		public final String name;
		public final float width;
		public final float height;

		public TmxObjectGroup(String name, float width, float height) {
			this.name = name;
			this.width = width;
			this.height = height;
			objects = new ArrayList<>();
		}
	}

	private final List<TmxObjectGroup> groups;
	private final float tileWidth, tileHeight;
	private final int mapwidth, mapheight;

	public TmxObjectsLoader(Element tmxRootElement) {
		this.groups = new ArrayList<>();
		Array<Element> objectgroups = tmxRootElement.getChildrenByName("objectgroup");
		for (Element objectgroupElement : objectgroups) {
			groups.add(parseObjectgroup(objectgroupElement));
		}
		this.tileWidth = tmxRootElement.getFloat("tilewidth", 32);
		this.tileHeight = tmxRootElement.getFloat("tileheight", 32);
		this.mapwidth = tmxRootElement.getInt("width", 50);
		this.mapheight = tmxRootElement.getInt("height", 50);
	}

    public List<TmxObjectGroup> getObjectGroups() {
        return groups;
    }

	public void loadToPhysics(Physics physics) {
		for (TmxObjectGroup group : groups)
			loadToPhysics(group, physics, tileWidth, tileHeight, mapwidth, mapheight);
	}

	public void loadToPhysics(TmxObjectGroup group, Physics physics) {
		for (TmxObject obj : group.objects)
			createBox2DBody(obj, physics, tileWidth, tileHeight, mapheight, mapwidth);
	}

	public void loadToPhysics(TmxObject object, Physics physics) {
		createBox2DBody(object, physics, tileWidth, tileHeight, mapheight, mapwidth);
	}

	public void loadToPhysics(Physics physics, float tileWidth, float tileHeight, int mapwidth, int mapheight) {
		for (TmxObjectGroup group : groups)
			loadToPhysics(group, physics, tileWidth, tileHeight, mapwidth, mapheight);
	}

	public void loadToPhysics(TmxObjectGroup group, Physics physics, float tileWidth, float tileHeight, int mapwidth, int mapheight) {
		for (TmxObject obj : group.objects)
			createBox2DBody(obj, physics, tileWidth, tileHeight, mapheight, mapwidth);
	}

	public void loadToPhysics(TmxObject object, Physics physics, float tileWidth, float tileHeight, int mapwidth, int mapheight) {
		createBox2DBody(object, physics, tileWidth, tileHeight, mapheight, mapwidth);
	}

	public TmxObject getObjectByName(String name, boolean ignoreCase) {
		for (TmxObjectGroup group : groups) {
			TmxObject obj = getObjectByName(group, name, ignoreCase);
			if (obj != null)
				return obj;
		}
		return null;
	}

	public TmxObject getObjectByName(TmxObjectGroup group, String name, boolean ignoreCase) {
		for (TmxObject obj : group.objects) {
			if (ignoreCase) {
				if (obj.name.equalsIgnoreCase(name))
					return obj;
			} else {
				if (obj.name.equals(name))
					return obj;
			}
		}
		return null;
	}

	public TmxObjectGroup getObjectGroupByName(String name, boolean ignoreCase) {
		for (TmxObjectGroup group : groups) {
			if (ignoreCase) {
				if (group.name.equalsIgnoreCase(name))
					return group;
			} else {
				if (group.name.equals(name))
					return group;
			}
		}
		return null;

	}

	public TmxObjectGroup parseObjectgroup(Element element) {
		TmxObjectGroup group = new TmxObjectGroup(
				element.get("name", ""),
				element.getFloat("width", 0),
				element.getFloat("height", 0));

		Array<Element> objs = element.getChildrenByName("object");
		for (Element obj : objs) {
			Type type = null;
			String points = "";
			if (obj.getChildCount() == 0) type = Type.RECTANGLE;
			else {
				switch (obj.getChild(0).getName().toLowerCase()) {
				case "polygon": type = Type.POLYGON; break;
				case "polyline": type = Type.POLYLINE; break;
				case "ellipse": type = Type.ELLIPSE; break;
				default: type = Type.RECTANGLE; break;
				}
				if (type == Type.POLYGON || type == Type.POLYLINE) {
					points = obj.getChild(0).getAttribute("points", "0,0");
				}
			}

			group.objects.add(new TmxObject(
					obj.get("name", ""),
					obj.get("type", ""),
					points,
					type,
					obj.getFloat("x", 0),
					obj.getFloat("y", 0),
					obj.getFloat("width", 0),
					obj.getFloat("height", 0)));
		}

		return group;
	}

	private void createBox2DBody(TmxObject object, Physics physics, float tileWidth, float tileHeight, int mapheight, int mapwidth) {
		// From Tiled to Box2D space (32 Pixels = 1 Meter):
		final float objectX = object.x / tileWidth;
		final float objectY = object.y / tileHeight;
		final float objectWidth = object.width / tileWidth;
		final float objectHeight = object.height / tileHeight;
		// From Tiled Rectangles to Box2D Rectangles
		// Difference:
		//  - Tiled Rectangles:
		//      Origin at top-left
		//      Width / Height from top-left to bottom-right
		//  - Box2D Rectangles:
		//      Origin in the middle
		//      Width / Height from the origin to an edge
		//      Upside down y coordinate, relative to Tiled
		final float w = (objectWidth / 2);// / (float) map.tileWidth;
		final float h = (objectHeight / 2);// / (float) map.tileHeight;
		final float x = ((objectX - (objectWidth / 2)) + w*2);// / (float) map.tileWidth;
		final float y = (mapheight) - (objectY - (objectHeight / 2) + h*2);// / (float) map.tileHeight;

		switch (object.objectType) {
		case RECTANGLE:
			Body box = physics.createBox(BodyType.StaticBody, w, h, 1);
			box.setTransform(x, y, 0f);
			break;
		case ELLIPSE:
			Body circle = physics.createCircle(BodyType.StaticBody, (w + h) / 2, 1);
			circle.setTransform(x, y, 0f);
			break;
		case POLYGON:
			createPolygon(object, physics, tileWidth, tileHeight, mapwidth, mapheight);
			break;
		case POLYLINE:
			createEdge(object, physics, tileWidth, tileHeight, mapwidth, mapheight);
			break;
		}
	}

	private Vector2[] readVectorsFromTiledString(TmxObject object, String points) {
		// Example for "points":
		// 0,0 -96,96 -64,224 64,192 96,0 192,128 256,-96 0,-192 0,0
		String[] pointStrs = points.split(" ");
		String[][] numStrs = new String[pointStrs.length][];
		for (int i = 0; i < pointStrs.length; i++) {
			numStrs[i] = pointStrs[i].split(",");
		}
		Vector2[] vecs = new Vector2[numStrs.length];
		for (int i = 0; i < numStrs.length; i++) {
			vecs[i] = new Vector2();
			vecs[i].x = Float.parseFloat(numStrs[i][0]);
			vecs[i].y = Float.parseFloat(numStrs[i][1]);
		}
		return vecs;
	}

	private void createEdge(TmxObject object, Physics physics, float tileWidth, float tileHeight, int mapheight, int mapwidth) {
		Vector2[] vecs = readVectorsFromTiledString(object, object.pointData);
		for (int i = 1; i < vecs.length; i++) {
			// In the first iteration: [0] and [1]
			// In the second iteration: [1] and [2]
			// ...
			Vector2 v0 = new Vector2(vecs[i-1]);
			v0.x = (object.x + v0.x) / tileWidth;
			v0.y = mapheight - (object.y + v0.y) / tileHeight;
			Vector2 v1 = new Vector2(vecs[i]);
			v1.x = (object.x + v1.x) / tileWidth;
			v1.y = mapheight - (object.y + v1.y) / tileHeight;
			physics.createEdge(BodyType.StaticBody, v0.x, v0.y, v1.x, v1.y, 1);
		}
	}

	private void createPolygon(TmxObject object, Physics physics, float tileWidth, float tileHeight, int mapheight, int mapwidth) {
		Vector2[] edges = readVectorsFromTiledString(object, object.pointData);
		if (edges.length <= 2) return;
		for (int i = 0; i < edges.length; i++) {
			Vector2 v = edges[i];
			v.x = v.x / tileWidth;
			v.y = object.height - v.y / tileHeight;
		}
		ArrayList<Vector2> edgesArray = new ArrayList<>();
		for (Vector2 edge : edges) {
			edgesArray.add(edge);
		}
		ArrayList<ArrayList<Vector2>> polyList = BayazitDecomposer.convexPartition(edgesArray);
		for (int i = 0; i < polyList.size(); i++) {
			ArrayList<Vector2> listPoly = polyList.get(i);
			Vector2[] poly = new Vector2[listPoly.size()];
			listPoly.toArray(poly);
			physics.createPolygon(BodyType.StaticBody,
					object.x / tileWidth,
					mapheight - object.y / tileHeight, poly, 1);
		}
	}

	public static void main(String... args) throws IOException {
		GdxNativesLoader.load();
		Physics physics = new Physics(Vector2.Zero, true);
		File mapfile = new File("data/maps/newmap/map005.tmx");
		Element mapXML = new XmlReader().parse(new FileInputStream(mapfile));
		System.out.println(mapXML);

		new TmxObjectsLoader(mapXML)
			.loadToPhysics(physics, 32, 32, 50, 50);
	}

}
