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

import org.matheusdev.ror.ClientMaster;
import org.matheusdev.ror.FollowingCamera;
import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.RuinsOfRevenge;
import org.matheusdev.ror.map.Map;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.util.Config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

	public static final float PIX_PER_METER = 32f / 1f;
	public static final float METER_PER_PIX = 1f / 32f;

	private boolean disposed;

	private final ClientMaster client;
	private final Map map;
	private final ResourceLoader res;
	private final FollowingCamera cam;
	private final OrthographicCamera hudCam;
	private final Box2DDebugRenderer debugRenderer;

	private final BitmapFont font = new BitmapFont();

	private PostProcessor processor;

	private boolean debugDraw;
	private int zoom;

	public ScreenGameMap(ResourceLoader res, RuinsOfRevenge game) {
		super(new Stage(), game);

		this.res = res;
		this.client = new ClientMaster(res, "data/entities/");
		this.map = new Map(Gdx.files.internal("data/maps/newmap/map004.tmx"), client.getPhysics());
		this.cam = new FollowingCamera(PIX_PER_METER);
		this.hudCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.debugRenderer = new Box2DDebugRenderer();

		ShaderLoader.BasePath = "data/shaders/";
		processor = rebuildProcessor();

		Entity player = client.addEntity("player").getEntity();
		player.getBody().setTransform(map.getSpawnpoint(), 0);
		cam.following = player;
	}

	public PostProcessor rebuildProcessor() {
		float screenw = Gdx.graphics.getWidth();
		float screenh = Gdx.graphics.getHeight();

		PostProcessor processor = new PostProcessor(false, true, true);
		Bloom.Settings settings = new Bloom.Settings(
				"blah",
				2,
				Config.get().bloomtreshold,
				Config.get().baseintensity,
				Config.get().basesaturation,
				Config.get().bloomintensity,
				Config.get().bloomsaturation);
		Bloom bloomEffect = new Bloom((int)(screenw/8), (int)(screenh/8));
		bloomEffect.setSettings(settings);
		if (Config.get().bloom) processor.addEffect(bloomEffect);
		processor.setClearColor(0.5f, 0.5f, 0.5f, 0f);

		return processor;
	}

	@Override
	public void tick(float delta) {
		client.tick(delta);
		stage.act(delta);
		cam.update();
	}

	@Override
	public void draw(SpriteBatch batch) {
		processor.capture();

		cam.loadToBatch(batch);

		map.renderBelowEntities(cam.getCam());

		batch.enableBlending();
		batch.begin();
		client.draw(batch, map.getFringeLayer(), Gdx.graphics.getDeltaTime());
		batch.end();

		map.renderAboveEntities(cam.getCam());

		processor.render();

		stage.draw();

		if (debugDraw)
			debugRenderer.render(client.getPhysics().getWorld(), cam.getCam().combined);

		drawHUD(batch);
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
		cam.resize(width, height);
		stage.setViewport(width, height, true);
		hudCam.viewportWidth = width;
		hudCam.viewportHeight = height;
		processor = rebuildProcessor();
	}

	@Override
	public void dispose() {
		if (!disposed) {
			disposed = true;
			map.dispose();
		}
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
		if (keycode == Config.get().key("escape")) {
			game.pushScreen(new ScreenPause(res, game));
			return false;
		} else if (keycode == Config.get().key("debugDraw")) {
			debugDraw = !debugDraw;
			System.out.println("Switched debug drawing " + (debugDraw ? "on" : "off"));
			return false;
		}
		return true;
	}

	@Override
	public boolean isParentVisible() {
		return false;
	}
}
