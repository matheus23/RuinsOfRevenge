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

import java.io.IOException;
import java.util.Stack;

import org.matheusdev.ror.screens.AbstractScreen;
import org.matheusdev.ror.screens.ScreenMenu;
import org.matheusdev.util.Config;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bitfire.utils.ShaderLoader;

/**
 * @author matheusdev
 *
 */
public class RuinsOfRevenge extends Game implements InputProcessor {

	public static void main(String[] args) {
		new LwjglApplication(
				new RuinsOfRevenge(),
				"matheusdev: Ruins Of Revenge",
				Config.get().resolutionX,
				Config.get().resolutionY,
				true);
	}

	private ResourceLoader res;
	private Stack<AbstractScreen> screens;
	private Stack<AbstractScreen> drawStack;

	private final Color darkenColor = new Color(0.1f, 0.1f, 0.1f, 0.8f);

	@Override
	public void create() {
		ShaderLoader.BasePath = "data/shaders/";
		screens = new Stack<>();
		drawStack = new Stack<>();
		try {
			res = new ResourceLoader(Gdx.files.internal("data/sprites/resources.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		pushScreen(new ScreenMenu(res, this));
		Gdx.graphics.setVSync(true);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render() {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        prepareDrawStack();
        updateDrawStack(Gdx.graphics.getDeltaTime());
	}

	private void prepareDrawStack() {
		for (int i = screens.size()-1; i >= 0; i--) {
			AbstractScreen screen = screens.get(i);
			drawStack.push(screen);

			if (!screen.isParentVisible()) {
				break;
			}
		}
	}

	private void updateDrawStack(float delta) {
		while (drawStack.size() > 1) {
			AbstractScreen screen = drawStack.pop();
			Stage stage = screen.getStage();
			// if it's the last element (topmost screen)
			stage.getSpriteBatch().setColor(Color.WHITE);
			screen.update(delta);
		}
		AbstractScreen topScreen = drawStack.pop();
		Stage stage = topScreen.getStage();
		stage.getSpriteBatch().enableBlending();
		stage.getSpriteBatch().setColor(darkenColor);
		stage.getSpriteBatch().begin();
		stage.getSpriteBatch().draw(res.getRegion("white"), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.getSpriteBatch().end();
		stage.getSpriteBatch().setColor(Color.WHITE);
		topScreen.update(delta);
	}

	@Override
	public void resize(int width, int height) {
		Gdx.gl.glViewport(0, 0, width, height);
		for (AbstractScreen screen : screens) {
			screen.resize(width, height);
		}
		Config.get().setRes(width, height);
	}

	public boolean shouldHaveInputFocus(AbstractScreen screen) {
		return screens.peek() == screen;
	}

	@Override
	public void dispose() {
		res.dispose();
	}

	public AbstractScreen popScreen() {
		AbstractScreen popped = screens.pop();
		return popped;
	}

	public void pushScreen(AbstractScreen screen) {
		screens.add(screen);
	}

	@Override
	public boolean keyDown(int keycode) {
		return screens.peek().keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		return screens.peek().keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		return screens.peek().keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return screens.peek().touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return screens.peek().touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return screens.peek().touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return screens.peek().mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
		return screens.peek().scrolled(amount);
	}

}
