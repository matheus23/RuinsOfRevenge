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
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.RuinsOfRevenge;
import org.matheusdev.ror.screens.gui.TouchUpListener;

/**
 * @author matheusdev
 *
 */
public class ScreenMenu extends AbstractScreen {

	private final InputListener playListener = new TouchUpListener() {
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			game.pushScreen(new ScreenConnect(resources,  game));
		}
	};
	private final InputListener settingsListener = new TouchUpListener() {
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			game.pushScreen(new ScreenSettings(resources, game));
		}
	};
	private final InputListener exitListener = new TouchUpListener() {
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			Gdx.app.exit();
		}
	};

	private final RuinsOfRevenge game;
	private final ResourceLoader resources;

	private final Skin skin;
	private final Table table;
	private final TextureRegion background;
	private final TextureRegion ruinsOfRevengeText;

	public ScreenMenu(final ResourceLoader resources, final RuinsOfRevenge game) {
		super(new Stage(), game);
		this.resources = resources;
		this.game = game;
		this.background = resources.getRegion("background");
		background.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.ruinsOfRevengeText = resources.getRegion("RuinsOfRevenge");

		skin = resources.getSkin("uiskin");

		Image rorLogo = new Image(ruinsOfRevengeText);
		TextButton play = new TextButton("Play", skin);
		TextButton settings = new TextButton("Settings", skin);
		TextButton exit = new TextButton("Exit", skin);

		play.addListener(playListener);
		settings.addListener(settingsListener);
		exit.addListener(exitListener);

		table = new Table(skin);
		table.add(rorLogo).size(600, 200).space(32);
		table.row();
		table.add(play).size(320, 64).space(8);
		table.row();
		table.add(settings).size(320, 64).space(8);
		table.row();
		table.add(exit).size(320, 64).space(8);
		table.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		stage.addActor(table);
	}

	@Override
	public void tick(float delta) {
		stage.act(delta);
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		stage.draw();
		Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
		table.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isParentVisible() {
		return false;
	}

}
