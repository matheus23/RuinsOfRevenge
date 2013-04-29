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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.utils.ShaderLoader;
import org.matheusdev.ror.FollowingCamera;
import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.RuinsOfRevenge;
import org.matheusdev.ror.client.ClientMaster;
import org.matheusdev.ror.map.Map;
import org.matheusdev.util.Config;
import org.matheusdev.util.KeysUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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
    private final TextField chatInput;
    private final Label chat;

	private PostProcessor processor;

	private boolean debugDraw;
    private boolean bloom = Config.get().bloom;
	private int zoom;

	public ScreenGameMap(ResourceLoader res, RuinsOfRevenge game, final ClientMaster client, String mapFile) {
		super(new Stage(), game);

		this.res = res;
        this.client = client;
		this.map = new Map(res.getFileLocation(), mapFile, client.getPhysics());
		this.cam = new FollowingCamera(PIX_PER_METER);
		this.hudCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.debugRenderer = new Box2DDebugRenderer();

		ShaderLoader.BasePath = "data/shaders/";
		processor = rebuildProcessor();
		client.initializeEntities(map.getSpawnpoint());
		cam.getCam().position.set(map.getSpawnpoint().x, map.getSpawnpoint().y, 0);

        Skin skin = res.getSkin("uiskin");

        chatInput = new TextField("", skin);
        chatInput.setColor(1, 1, 1, 0.6f);
        chat = new Label("Ruins of Revenge", skin);

        Table table = new Table(skin);
        table.setFillParent(true);
        table.add(chat).expand().width(400).space(8).bottom().left();
        table.row();
        table.add(chatInput).expandX().width(400).space(8).bottom().left();

        stage.addActor(table);
        chatInput.addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Capture a click-on-chat event in capture phase and kill the event,
                // before the stage resets Keyboard focus:
                stage.setKeyboardFocus(chatInput);
                event.cancel();
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return true;
            }
        });
        chatInput.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if (key == 27 /* ASCII Escape */) {
                    if (!chatInput.getText().isEmpty())
                        chatInput.setText("");
                } else if (key == '\n' || key == '\r') {
                    if (!chatInput.getText().isEmpty()) {
                        client.inputChat(chatInput.getText());
                        chatInput.setText("");
                    }
                }
            }
        });
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                stage.setKeyboardFocus(null);
            }
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return true;
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Config.get().key("chat")) {
                    stage.setKeyboardFocus(chatInput);
                }
                return false;
            }
        });
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

        bloom = Config.get().bloom;

		return processor;
	}

	@Override
	public void tick(float delta) {
		if (client.getPlayer() != null)
			cam.following = client.getPlayer().getEntity();
		client.tick(stage.getKeyboardFocus() != chatInput, delta);
		stage.act(delta);
		cam.update();
	}

	@Override
	public void draw(SpriteBatch batch) {
        if (bloom != Config.get().bloom) {
            processor = rebuildProcessor();
        }
		processor.capture();

		cam.loadToBatch(batch);

		map.renderBelowEntities(cam.getCam());

		batch.enableBlending();
		batch.begin();
		client.draw(batch, map.getFringeLayer(), Gdx.graphics.getDeltaTime());
		batch.end();

		map.renderAboveEntities(cam.getCam());

		processor.render();

		if (debugDraw)
			debugRenderer.render(client.getPhysics().getWorld(), cam.getCam().combined);

		drawHUD(batch);
	}

	public void drawHUD(SpriteBatch batch) {
        // Update chat text:
        if (client.chatChanged()) updateChat();
        // draw Stage:
        stage.draw();
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

    // This has to be revisited:
    // This is really an incredible mess... wow...
    // <revisit>
    private final StringBuilder chatBuilder = new StringBuilder();
    private final Stack<String> chatBuildStack = new Stack<String>();

    public void updateChat() {
        if (chatBuilder.length() > 0)
            chatBuilder.delete(0, chatBuilder.length()-1);
        chatBuildStack.clear();

        List<String> lines = client.getChat();
        BitmapFont font = chat.getStyle().font;
        for (int i = Math.max(0, lines.size()-10); i < lines.size();) {
            String line = lines.get(i);
            Stack<String> split = splitIntoLines(line, font, 400);

            while (!split.empty()) {
                chatBuildStack.push(split.pop());
                i++;
            }
        }
        while (!chatBuildStack.empty()) {
            chatBuilder.append(chatBuildStack.pop());
            chatBuilder.append('\n');
        }
        chat.setText(chatBuilder);
    }

    private Stack<String> splitIntoLines(String line, BitmapFont font, float width) {
        return splitIntoLinesRec(new Stack<String>(), line, font, width);
    }

    private Stack<String> splitIntoLinesRec(Stack<String> list, String line, BitmapFont font, float width) {
        int visible = font.computeVisibleGlyphs(line, 0, line.length(), width);
        if (line.length() > visible) {
            String head = line.substring(0, visible-1);
            String rest = line.substring(visible, line.length()-1);
            list.push(head);
            return splitIntoLinesRec(list, rest, font, width);
        } else {
            list.push(line);
            return list;
        }
    }
    // </revisit>

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
			client.dispose();
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
		return super.scrolled(amount);
	}

	@Override
	public boolean keyDown(int keycode) {
        return stage.getKeyboardFocus() != chatInput || super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Config.get().key("escape") && stage.getKeyboardFocus() != chatInput) {
            System.out.println("focus: " + stage.getKeyboardFocus() + ", chat: " + chatInput);
            game.pushScreen(new ScreenPause(res, game));
			return false;
		} else if (keycode == Config.get().key("debugDraw")) {
			debugDraw = !debugDraw;
			System.out.println("Switched debug drawing " + (debugDraw ? "on" : "off"));
			return false;
		}
		return super.keyUp(keycode);
	}

	@Override
	public boolean isParentVisible() {
		return false;
	}
}
