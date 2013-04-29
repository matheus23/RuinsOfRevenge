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

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.RuinsOfRevenge;
import org.matheusdev.util.ExceptionUtils;

/**
 * @author matheusdev
 *
 */
public class ScreenError extends AbstractScreen {

	private final InputListener backListener = new InputListener() {
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			return true;
		}
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			game.popScreen();
		}
	};
	private final InputListener submitListener = new InputListener() {
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			return true;
		}
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			game.pushScreen(new ScreenError(resources, game, "Feature not implemented!", null));
		}
	};

	private final RuinsOfRevenge game;
	private final ResourceLoader resources;

	private final Skin skin;

	public ScreenError(final ResourceLoader resources, final RuinsOfRevenge game, String message, final Exception e) {
		super(new Stage(), game);
		this.resources = resources;
		this.game = game;

		skin = resources.getSkin("uiskin");

		final Dialog dialog = new Dialog("Error", skin);

		TextButton back = new TextButton("Back", skin);
		TextButton submit = new TextButton("Sumbit", skin);

		back.addListener(backListener);
		submit.addListener(submitListener);

		dialog.getContentTable().pad(8);
		dialog.getButtonTable().pad(8);

		dialog.getContentTable().add(message).space(8);
		dialog.getContentTable().row();
		dialog.getContentTable().add("Details: ").space(8);
		dialog.getContentTable().row();
		if (e != null) {
			Label excText = new Label(ExceptionUtils.stackTraceToString(e), skin);
			ScrollPane pane = new ScrollPane(excText, skin);
			pane.setFadeScrollBars(false);
			pane.setOverscroll(false, true);
			dialog.getContentTable().add(pane).height(100).space(8);
			dialog.getContentTable().row();
		}
		dialog.getButtonTable().add(back).size(160, 32).space(8);
		dialog.getButtonTable().add(submit).size(160, 32).space(8);

		dialog.setKeepWithinStage(true);
		dialog.show(stage);

		dialog.getContentTable().debug();
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
	}

	@Override
	public boolean isParentVisible() {
		return true;
	}

}
