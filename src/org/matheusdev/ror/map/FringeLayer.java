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

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author matheusdev
 *
 */
public class FringeLayer {

	private class FringeTile implements Comparable<FringeTile> {
        TiledMapTileLayer.Cell cell;
		int x, y;
		float yoffset;

		FringeTile(TiledMapTileLayer.Cell cell, int x, int y, float yoffset) {
            this.cell = cell;
			this.x = x;
			this.y = y;
			this.yoffset = yoffset;
		}

		float height() {
			return y + yoffset;
		}

		@Override
		public int compareTo(FringeTile o) {
			if (o.height() > height()) {
				return 1;
			} else if (o.height() < height()) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	private final TiledMap map;
	private final TiledMapTileLayer layer;
	private final List<FringeTile> tiles;

	private int currentInd;

	public FringeLayer(TiledMap map, TiledMapTileLayer fringeLayer) {
		this.map = map;
		this.layer = fringeLayer;
		this.tiles = new ArrayList<>();

		for (int x = 0; x < layer.getWidth(); x++) {
			for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
				if (cell == null) continue;
				String yoffsetStr = cell.getTile().getProperties().get("yoffset", String.class);

				if (yoffsetStr == null || yoffsetStr.isEmpty()) {
					System.out.println("Warning - Fringe tile at [" + x + ", " + y + "] missing property <yoffset>");
				} else {
					try {
						tiles.add(new FringeTile(
								cell,
								x, y,
								Float.parseFloat(yoffsetStr)));
					} catch (NumberFormatException e) {
						System.err.println("Can't parse \"" + yoffsetStr + "\" as float: " + e);
					}
				}
			}
		}
		Collections.sort(tiles);
	}

	public void begin() {
		currentInd = 0;
	}

	public void renderTill(SpriteBatch batch, float y) {
		for (int i = currentInd; i < tiles.size(); i++) {
			FringeTile tile = tiles.get(i);
			currentInd = i;
			if (tile.height() < y) {
				return;
			}
            batch.draw(tile.cell.getTile().getTextureRegion(), tile.x, tile.y, 1f, 1f);
		}
		// Only if all tiles were drawn (see return; above)
		currentInd++;
	}

	public void end(SpriteBatch batch) {
		for (int i = currentInd; i < tiles.size(); i++) {
			FringeTile tile = tiles.get(i);
            batch.draw(tile.cell.getTile().getTextureRegion(), tile.x, tile.y, 1f, 1f);
		}
	}

}
