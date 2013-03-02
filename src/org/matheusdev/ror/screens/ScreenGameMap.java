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
package org.matheusdev.ror.screens;

import org.matheusdev.ror.FollowingCamera;
import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.collision.Physics;
import org.matheusdev.ror.entity.Entity;
import org.matheusdev.ror.entity.EntityBall;
import org.matheusdev.ror.entity.EntityManager;
import org.matheusdev.ror.entity.EntityPlayer;
import org.matheusdev.ror.map.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.utils.ShaderLoader;

/**
 * @author matheusdev
 *
 */
public class ScreenGameMap implements Screen {

	private boolean disposed;

	private final Map map;
	private final Physics physics;
	private final EntityManager entityManager;
	private final SpriteBatch batch;

	private final Box2DDebugRenderer debugRend;
	private final FollowingCamera cam;
	private final OrthographicCamera hudCam;

	private final BitmapFont font = new BitmapFont();

	public final float camspeed = (float) Math.PI / 10f; // Just for fun...
	public final float PIX_PER_METER = 32 / 1;
	public final float METER_PER_PIX = 1 / 32;

	private PostProcessor processor;
	public boolean debugDraw;
	public boolean pressedF8;
	public boolean bloom = true;
	public boolean pressedB;

	public ScreenGameMap(FileHandle mapfile, ResourceLoader res) {
		this.physics = new Physics(new Vector2(0, 0), true);
		this.map = new Map(mapfile, physics);

		float screenw = Gdx.graphics.getWidth();
		float screenh = Gdx.graphics.getHeight();
		this.cam = new FollowingCamera(PIX_PER_METER);
		this.hudCam = new OrthographicCamera(screenw, screenh);

		this.batch = new SpriteBatch();
		this.entityManager = new EntityManager(physics, res);

		ShaderLoader.BasePath = "data/shaders/";
		this.processor = rebuildProcessor();

		debugRend = new Box2DDebugRenderer();

		Vector2 spawn = map.getSpawnpoint();
		Entity entity = new EntityPlayer(spawn.x, spawn.y, entityManager);
		entityManager.add(entity);
		cam.following = entity;
	}

	public PostProcessor rebuildProcessor() {
		float screenw = Gdx.graphics.getWidth();
		float screenh = Gdx.graphics.getHeight();

		PostProcessor processor = new PostProcessor(false, true, true);
		if (bloom) processor.addEffect(new Bloom((int)(screenw/8), (int)(screenh/8)));
		processor.setClearColor(0.5f, 0.5f, 0.5f, 0f);

		return processor;
	}

	private final Vector3 worldSpaceMouse = new Vector3();
	private final Vector3 screenSpaceMouse = new Vector3();

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		tick(delta);
		draw(batch);
	}

	public void tick(float delta) {
		updateMouse();
		updateInput();
		updateEntities(delta);
		updatePhysics(delta);
	}

	public void updateMouse() {
		// Compute mouse in different spaces:
		screenSpaceMouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		worldSpaceMouse.set(screenSpaceMouse);
		cam.getCam().unproject(worldSpaceMouse);
	}

	public void updateInput() {
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			entityManager.add(new EntityBall(worldSpaceMouse.x, worldSpaceMouse.y, entityManager));
		}
		if (Gdx.input.isKeyPressed(Keys.I)) {
			cam.getCam().zoom /= 1.05f;
		}
		if (Gdx.input.isKeyPressed(Keys.K)) {
			cam.getCam().zoom *= 1.05f;
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_1)) {
			cam.getCam().zoom = 1/1f;
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_2)) {
			cam.getCam().zoom = 1/2f;
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_3)) {
			cam.getCam().zoom = 1/3f;
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_4)) {
			cam.getCam().zoom = 1/4f;
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_5)) {
			cam.getCam().zoom = 1/5f;
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_6)) {
			cam.getCam().zoom = 1/6f;
		}
		if (Gdx.input.isKeyPressed(Keys.F8) && !pressedF8) {
			pressedF8 = true;
			debugDraw = !debugDraw;
			System.out.println("Switched debug drawing " + (debugDraw ? "on" : "off"));
		} else if (!Gdx.input.isKeyPressed(Keys.F8) && pressedF8) {
			pressedF8 = false;
		}
		if (Gdx.input.isKeyPressed(Keys.B) && !pressedB) {
			pressedB = true;
			bloom = !bloom;
			processor = rebuildProcessor();
			System.out.println("Switched bloom " + (bloom ? "on" : "off"));
		} else if (!Gdx.input.isKeyPressed(Keys.B) && pressedB) {
			pressedB = false;
		}
	}

	public void updateEntities(float delta) {
		entityManager.tick(delta);
	}

	public void updatePhysics(float delta) {
		physics.step(delta);
	}

	public void draw(SpriteBatch batch) {
		processor.capture();
		drawWorld(batch);
		processor.render();
		drawHUD(batch);
	}

	public void drawWorld(SpriteBatch batch) {
		// Rendering:
		cam.update();
		//   First, the Tiled map below entities (ground, etc):
		map.renderBelowEntities(cam.getCam());
		//   Then the objects:
		//     Setup the SpriteBatch:
		cam.loadToBatch(batch);
		//     Render the Entities:
		batch.begin();
		map.beginFringe();
		entityManager.draw(batch, map);
		map.endFringe(batch);
		batch.end();
		// Render layers above entities:
		map.renderAboveEntities(cam.getCam());

		if (debugDraw) {
			// Render the Box2D stuff:
			debugRend.render(physics.getWorld(), cam.getCam().combined);
		}
	}

	public void drawHUD(SpriteBatch batch) {
		// Render the HUD:
		hudCam.update();
		batch.setProjectionMatrix(hudCam.projection);
		batch.setTransformMatrix(hudCam.view);
		batch.begin();
		float x = -Gdx.graphics.getWidth() / 2f + 5f;
		float y = Gdx.graphics.getHeight() / 2f - 5f;
		// The only thing on the HUD is the FPS:
		font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), x, y);
		batch.end();
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		processor = rebuildProcessor();
		cam.resize(width, height);
		hudCam.viewportWidth = width;
		hudCam.viewportHeight = height;
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {
		processor.rebind();
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		if (!disposed) {
			disposed = true;
			processor.dispose();
			map.dispose();
			batch.dispose();
			physics.dispose();
		}
	}

}
