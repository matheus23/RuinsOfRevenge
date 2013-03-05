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
import org.matheusdev.ror.RuinsOfRevenge;
import org.matheusdev.ror.collision.Physics;
import org.matheusdev.ror.entity.Entity;
import org.matheusdev.ror.entity.EntityBall;
import org.matheusdev.ror.entity.EntityManager;
import org.matheusdev.ror.entity.EntityPlayer;
import org.matheusdev.ror.map.Map;
import org.matheusdev.util.Config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.utils.ShaderLoader;

/**
 * @author matheusdev
 *
 */
public class ScreenGameMap extends AbstractScreen {

	private boolean disposed;

	private final RuinsOfRevenge game;
	private final ResourceLoader res;

	private final Map map;
	private final Physics physics;
	private final EntityManager entityManager;
	private PostProcessor processor;

	private final Box2DDebugRenderer debugRend;
	private final FollowingCamera cam;
	private final OrthographicCamera hudCam;

	private final BitmapFont font = new BitmapFont();

	public final float camspeed = (float) Math.PI / 10f; // Just for fun...
	public final float PIX_PER_METER = 32 / 1;
	public final float METER_PER_PIX = 1 / 32;

	private final Vector3 worldSpaceMouse = new Vector3();
	private final Vector3 screenSpaceMouse = new Vector3();

	private int zoom;
	private boolean debugDraw;
	private boolean bloom = Config.get().bloom;

	public ScreenGameMap(ResourceLoader res, RuinsOfRevenge game) {
		super(new Stage(), game);
		stage.clear();
		this.res = res;
		this.game = game;

		FileHandle mapfile = Gdx.files.internal("data/maps/newmap/map004.tmx");

		this.physics = new Physics(new Vector2(0, 0), true);
		this.map = new Map(mapfile, physics);

		float screenw = Gdx.graphics.getWidth();
		float screenh = Gdx.graphics.getHeight();
		this.cam = new FollowingCamera(PIX_PER_METER);
		this.hudCam = new OrthographicCamera(screenw, screenh);

		this.entityManager = new EntityManager(physics, res);

		ShaderLoader.BasePath = "data/shaders/";
		this.processor = rebuildProcessor();

		debugRend = new Box2DDebugRenderer();

		Vector2 spawn = map.getSpawnpoint();
		Entity entity = new EntityPlayer(spawn.x, spawn.y, entityManager);
		entityManager.add(entity);
		cam.following = entity;
	}

	float baseintensity = 1f;
	float basesaturation = .85f;
	float bloomtreshold = 0.6f;//0.577f;
	float bloomintensity = 2.0f;
	float bloomsaturation = .85f;
	float blurammount = 2f;

	public PostProcessor rebuildProcessor() {
		float screenw = Gdx.graphics.getWidth();
		float screenh = Gdx.graphics.getHeight();

		PostProcessor processor = new PostProcessor(false, true, true);
		Bloom.Settings settings = new Bloom.Settings(
				"blah",
				2,
				bloomtreshold,
				baseintensity,
				basesaturation,
				bloomintensity,
				bloomsaturation);
		Bloom bloomEffect = new Bloom((int)(screenw/8), (int)(screenh/8));
		bloomEffect.setSettings(settings);
		if (bloom) processor.addEffect(bloomEffect);
		processor.setClearColor(0.5f, 0.5f, 0.5f, 0f);

		return processor;
	}

	@Override
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

	public float upOrDown(float ammount, int keyup, int keydown) {
		if (Gdx.input.isButtonPressed(keyup)) {
			return ammount;
		}
		if (Gdx.input.isButtonPressed(keydown)) {
			return -ammount;
		}
		return 0f;
	}

	public void updateInput() {
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			entityManager.add(new EntityBall(worldSpaceMouse.x, worldSpaceMouse.y, entityManager));
		}
	}

	public void updateEntities(float delta) {
		entityManager.tick(delta);
	}

	public void updatePhysics(float delta) {
		physics.step(delta);
	}

	@Override
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

	@Override
	public void resize(int width, int height) {
		processor = rebuildProcessor();
		cam.resize(width, height);
		hudCam.viewportWidth = width;
		hudCam.viewportHeight = height;
	}

	@Override
	public void dispose() {
		if (!disposed) {
			disposed = true;
			processor.dispose();
			map.dispose();
			physics.dispose();
			super.dispose();
		}
	}

	@Override
	public boolean isParentVisible() {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (amount > 0) {
			amount = 1;
		} else {
			amount = -1;
		}
		zoom -= amount;
		zoom = Math.min(Math.max(1, zoom), 10);
		cam.getCam().zoom = 1f / zoom;
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Config.get().key("debugDraw")) {
			debugDraw = !debugDraw;
			System.out.println("Switched debug drawing " + (debugDraw ? "on" : "off"));
			return false;
		} else if (keycode == Config.get().key("escape")) {
			game.pushScreen(new ScreenPause(res, game));
			return false;
		}
		return true;
	}

}
