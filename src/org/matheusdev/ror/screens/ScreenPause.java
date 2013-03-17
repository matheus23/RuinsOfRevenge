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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.RuinsOfRevenge;

/**
 * @author matheusdev
 *
 */
public class ScreenPause extends AbstractScreen {

	private final InputListener continueListener = new InputListener() {
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			return true;
		}
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			game.popScreen();
		}
	};
	private final InputListener settingsListener = new InputListener() {
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			return true;
		}
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			game.pushScreen(new ScreenSettings(resources, game));
		}
	};
	private final InputListener backToMMListener = new InputListener() {
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			return true;
		}
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			game.popScreen();
			game.popScreen();
		}
	};

	private final RuinsOfRevenge game;
	private final ResourceLoader resources;

	private final Skin skin;
	private final Table table;

	public ScreenPause(final ResourceLoader resources, final RuinsOfRevenge game) {
		super(new Stage(), game);
		this.resources = resources;
		this.game = game;

		skin = resources.getSkin("uiskin");

		TextButton play = new TextButton("Continue Playing", skin);
		TextButton settings = new TextButton("Settings", skin);
		TextButton backToMM = new TextButton("Back to Main Menu", skin);

		play.addListener(continueListener);
		settings.addListener(settingsListener);
		backToMM.addListener(backToMMListener);

		table = new Table(skin);
		table.add(play).size(320, 64).space(8);
		table.row();
		table.add(settings).size(320, 64).space(8);
		table.row();
		table.add(backToMM).size(320, 64).space(8);
		table.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		stage.addActor(table);
		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				return true;
			}

			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				game.popScreen();
				return false;
			}
		});
	}

	@Override
	public void tick(float delta) {
		stage.act(delta);
	}

	@Override
	public void draw(SpriteBatch batch) {
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
		table.setPosition(width/2f, height/2f);
	}

	@Override
	public boolean isParentVisible() {
		return true;
	}

}
