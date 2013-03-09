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
package org.matheusdev.ror.map;

import java.io.IOException;

import org.matheusdev.ror.collision.Physics;
import org.matheusdev.util.TmxObjectsLoader;
import org.matheusdev.util.TmxObjectsLoader.TmxObject;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.SimpleTileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.XmlReader;


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

		try {
			TmxObjectsLoader objs = new TmxObjectsLoader(new XmlReader().parse(mapfile).getChildByName("map"));

			TmxObject spawnpointObj = objs.getObjectByName("spawnpoint", true);
			if (spawnpointObj != null)
				spawnpoint.set(spawnpointObj.x, spawnpointObj.y);

			if (physics != null) {
				objs.loadToPhysics(physics, map.tileWidth, map.tileHeight, map.width, map.height);
			}
		} catch (IOException e) {
			e.printStackTrace();
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

	public FringeLayer getFringeLayer() {
		return fringeLayer;
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

}
