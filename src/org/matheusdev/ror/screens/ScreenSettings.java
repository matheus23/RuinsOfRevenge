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

import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.RuinsOfRevenge;
import org.matheusdev.util.Config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * @author matheusdev
 *
 */
public class ScreenSettings extends AbstractScreen {

	private final RuinsOfRevenge game;
	private final ResourceLoader res;
	private final Table table;

	public ScreenSettings(final ResourceLoader res, final RuinsOfRevenge game) {
		super(new Stage());
		this.res = res;
		this.game = game;

		Skin skin = res.getSkin("uiskin");

		final CheckBox bloomSwitch = new CheckBox(" Enable Bloom", skin);
		final TextButton applyAndSave = new TextButton("Apply and Save", skin);
		final TextButton back = new TextButton("Back", skin);
		bloomSwitch.setChecked(Config.get().bloom);
		bloomSwitch.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
				Config.get().bloom = bloomSwitch.isChecked();
			}
		});
		back.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
				game.popScreen();
			}
		});

		table = new Table(skin);
		table.add(bloomSwitch).space(8);
		table.row();
		table.add(applyAndSave).size(160, 64).space(8);
		table.add(back).size(160, 64).space(8);
		table.row();
		table.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);

		stage.addActor(table);
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
	public void dispose() {
	}

	@Override
	public boolean isParentVisible() {
		return true;
	}

}
