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
package org.matheusdev.ror.map;

import java.util.ArrayList;

import org.matheusdev.ror.collision.Physics;
import org.matheusdev.util.BayazitDecomposer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.tiled.SimpleTileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;


/**
 * @author matheusdev
 *
 */
public class Map implements Disposable {

	// Disposable
	private boolean disposed = false;

	private final TiledMap map;
	private final TileAtlas atlas;
	private final TileMapRenderer renderer;
	private final Vector2 spawnpoint = new Vector2(2, 2);
	private final int fringeLayerIndex;
	private final int[] belowEntities;
	private final int[] aboveEntities;
	private final FringeLayer fringeLayer;

	public Map(FileHandle mapfile) {
		this(mapfile, null);
	}

	public Map(FileHandle mapfile, Physics physics) {
		map = TiledLoader.createMap(mapfile);

		// SimpleTileAtlas searches in the directory, in which
		// the .tmx map itself is, too:
		atlas = new SimpleTileAtlas(map, mapfile.parent());

		fringeLayerIndex = computeEntityLayer();
		if (fringeLayerIndex == 999) {
			belowEntities = new int[map.layers.size()];
			aboveEntities = new int[0];
			fillCounting(0, belowEntities);
			fringeLayer = null;
		} else {
			belowEntities = new int[fringeLayerIndex];
			aboveEntities = new int[map.layers.size()-fringeLayerIndex-1];
			fillCounting(0, belowEntities);
			fillCounting(fringeLayerIndex+1, aboveEntities);
			fringeLayer = new FringeLayer(map, map.layers.get(fringeLayerIndex), atlas);
		}

		renderer = new TileMapRenderer(map, atlas, 16, 16, 1f, 1f);

		if (physics != null) {
			createBox2DBodies(physics);
		}
	}

	private void fillCounting(int start, int[] array) {
		for (int i = 0, val = start; i < array.length; i++, val++) {
			array[i] = val;
		}
	}

	private int computeEntityLayer() {
		for (int i = 0; i < map.layers.size(); i++) {
			if (map.layers.get(i).name.equalsIgnoreCase("fringe")) {
				return i;
			}
		}
		return 999;
	}

	public void beginFringe() {
		if (fringeLayer != null) fringeLayer.begin();
	}

	public void renderTill(SpriteBatch batch, float y) {
		if (fringeLayer != null) fringeLayer.renderTill(batch, y);
	}

	public void endFringe(SpriteBatch batch) {
		if (fringeLayer != null) fringeLayer.end(batch);
	}

	public void renderBelowEntities(OrthographicCamera cam) {
		renderer.render(cam, belowEntities);
	}

	public void renderAboveEntities(OrthographicCamera cam) {
		renderer.render(cam, aboveEntities);
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.utils.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		if (!disposed) {
			renderer.dispose();
			atlas.dispose();
			disposed = true;
		}
	}

	/**
	 * Returns the Spawnpoint in World space: Meters
	 */
	public Vector2 getSpawnpoint() {
		return spawnpoint;
	}

	private void createBox2DBodies(Physics physics) {
		for (TiledObjectGroup group : map.objectGroups) {
			for (TiledObject object : group.objects) {
				if (object.name != null && object.name.equalsIgnoreCase("spawnpoint")) {
					spawnpoint.set(object.x / (float) map.tileWidth, map.height - object.y / (float) map.tileHeight);
				} else {
					// From Tiled to Box2D space (32 Pixels = 1 Meter):
					final float objectX = object.x / (float) map.tileWidth;
					final float objectY = object.y / (float) map.tileHeight;
					final float objectWidth = object.width / (float) map.tileWidth;
					final float objectHeight = object.height / (float) map.tileHeight;
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
					final float y = (map.height) - (objectY - (objectHeight / 2) + h*2);// / (float) map.tileHeight;

					if (object.polygon != null) {
						createPolygon(object, physics);
					} else if (object.polyline != null) {
						createEdge(object, physics);
					} else if (object.type != null && object.type.equalsIgnoreCase("circle")) {
						Body circle = physics.createCircle(BodyType.StaticBody, (w + h) / 2, 1);
						circle.setTransform(x, y, 0f);
					} else {
						Body box = physics.createBox(BodyType.StaticBody, w, h, 1);
						box.setTransform(x, y, 0f);
					}
				}
			}
		}
	}

	private Vector2[] readVectorsFromTiledString(TiledObject object, String points) {
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

	private void createEdge(TiledObject object, Physics physics) {
		Vector2[] vecs = readVectorsFromTiledString(object, object.polyline);
		for (int i = 1; i < vecs.length; i++) {
			// In the first iteration: [0] and [1]
			// In the second iteration: [1] and [2]
			// ...
			Vector2 v0 = new Vector2(vecs[i-1]);
			v0.x = (object.x + v0.x) / map.tileWidth;
			v0.y = map.height - (object.y + v0.y) / map.tileHeight;
			Vector2 v1 = new Vector2(vecs[i]);
			v1.x = (object.x + v1.x) / map.tileWidth;
			v1.y = map.height - (object.y + v1.y) / map.tileHeight;
			physics.createEdge(BodyType.StaticBody, v0.x, v0.y, v1.x, v1.y, 1);
		}
	}

	private void createPolygon(TiledObject object, Physics physics) {
		Vector2[] edges = readVectorsFromTiledString(object, object.polygon);
		if (edges.length <= 2) return;
		for (int i = 0; i < edges.length; i++) {
			Vector2 v = edges[i];
			v.x = v.x / map.tileWidth;
			v.y = object.height - v.y / map.tileHeight;
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
					object.x / (float) map.tileWidth,
					map.height - object.y / (float) map.tileHeight, poly, 1);
		}
	}

}
