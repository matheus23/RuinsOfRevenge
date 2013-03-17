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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.matheusdev.ror.model.entity.Entity;

/**
 * @author matheusdev
 *
 */
public class FollowingCamera {

	private final OrthographicCamera cam;
	private final float pixPerMeter;

	public Entity following;

	public FollowingCamera(float pixPerMeter) {
		float screenw = Gdx.graphics.getWidth();
		float screenh = Gdx.graphics.getHeight();
		this.cam = new OrthographicCamera(screenw / pixPerMeter, screenh / pixPerMeter);
		this.pixPerMeter = pixPerMeter;
	}

	public void update() {
		if (following != null) {
			Vector2 pos = following.getPos();
			cam.position.set(pos.x, pos.y, 0);
		}
		cam.update();
	}

	public void resize(float width, float height) {
		cam.viewportWidth = width / pixPerMeter;
		cam.viewportHeight = height / pixPerMeter;
	}

	public void loadToBatch(SpriteBatch batch) {
		batch.setProjectionMatrix(cam.projection);
		batch.setTransformMatrix(cam.view);
	}

	public OrthographicCamera getCam() {
		return cam;
	}

}