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

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.XmlReader;
import org.matheusdev.ror.collision.Physics;
import org.matheusdev.util.FileLocation;
import org.matheusdev.util.TmxObjectsLoader;

import java.io.IOException;


/**
 * @author matheusdev
 *
 */
public class Map implements Disposable {

	// Disposable
	private boolean disposed = false;

	private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;
	private final Vector2 spawnpoint = new Vector2(2, 2);
	private final int fringeLayerIndex;
	private final int[] belowEntities;
	private final int[] aboveEntities;
	private final FringeLayer fringeLayer;

	public Map(FileLocation loc, String mapfile) {
		this(loc, mapfile, null);
	}

	public Map(FileLocation loc, String mapfile, Physics physics) {
        TmxObjectsLoader objs = null;
        try {
            objs = new TmxObjectsLoader(new XmlReader().parse(loc.getFile(mapfile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
		this.map = new TmxMapLoader(loc.getResolver()).load(mapfile);
        this.renderer = new OrthogonalTiledMapRenderer(map, 1f / objs.getTileWidth());

		fringeLayerIndex = computeEntityLayer();
		if (fringeLayerIndex == 999) {
			belowEntities = new int[map.getLayers().getCount()];
			aboveEntities = new int[0];
			fillCounting(0, belowEntities);
			fringeLayer = null;
		} else {
			belowEntities = new int[fringeLayerIndex];
			aboveEntities = new int[map.getLayers().getCount()-fringeLayerIndex-1];
			fillCounting(0, belowEntities);
			fillCounting(fringeLayerIndex+1, aboveEntities);
            MapLayer mapLayer = map.getLayers().get(fringeLayerIndex);
            if (mapLayer instanceof TiledMapTileLayer) {
                fringeLayer = new FringeLayer(map, (TiledMapTileLayer) mapLayer);
            } else {
                fringeLayer = null;
            }
		}

        if (physics != null) {
            for (TmxObjectsLoader.TmxObjectGroup group : objs.getObjectGroups()) {
                for (TmxObjectsLoader.TmxObject obj : group.objects) {
                    if (!obj.name.equalsIgnoreCase("spawnpoint")) {
                        objs.loadToPhysics(obj, physics);
                    } else {
                        spawnpoint.set(obj.x / objs.getTileWidth(), obj.y / objs.getTileHeight());
                    }
                }
            }
        }
	}

	private void fillCounting(int start, int[] array) {
		for (int i = 0, val = start; i < array.length; i++, val++) {
			array[i] = val;
		}
	}

	private int computeEntityLayer() {
		for (int i = 0; i < map.getLayers().getCount(); i++) {
			if (map.getLayers().get(i).getName().equalsIgnoreCase("fringe")) {
				return i;
			}
		}
		return 999;
	}

	public FringeLayer getFringeLayer() {
		return fringeLayer;
	}

	public void renderBelowEntities(OrthographicCamera cam) {
        renderer.setView(cam);
		renderer.render(belowEntities);
	}

	public void renderAboveEntities(OrthographicCamera cam) {
        renderer.setView(cam);
		renderer.render(aboveEntities);
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.utils.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		if (!disposed) {
			renderer.dispose();
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
