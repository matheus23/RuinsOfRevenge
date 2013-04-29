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
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.RuinsOfRevenge;
import org.matheusdev.ror.screens.gui.TouchUpListener;
import org.matheusdev.util.Config;
import org.matheusdev.util.FloatUtils;
import org.matheusdev.util.KeysUtil;

/**
 * @author matheusdev
 *
 */
public class ScreenSettings extends AbstractScreen {

	private final class KeyInputListener extends InputListener {
		private final String key;
		private final TextButton button;

		public KeyInputListener(String key, TextButton button) {
			this.key = key;
			this.button = button;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			return true;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			if (this.button.isChecked()) {
				stage.setKeyboardFocus(this.button);
			} else {
				stage.setKeyboardFocus(null);
			}
		}

		@Override
		public boolean keyDown(InputEvent event, int keycode) {
			return true;
		}

		@Override
		public boolean keyUp(InputEvent event, int keycode) {
			if (this.button.isChecked()) {
				Config.get().key(key, keycode);
				this.button.setText(KeysUtil.forVal(Config.get().key(key)));
				this.button.setChecked(false);
			}
			return false;
		}
	}

//	private final RuinsOfRevenge game;
//	private final ResourceLoader res;
	private final Table table;
	private final TextButton gamepadButton;
	private final TextButton gamepadX;
	private final TextButton gamepadY;

	private boolean gamepadRead;
	private boolean gamepadXAxisRead;
	private boolean gamepadYAxisRead;
	private Controller gamepad;

	public ScreenSettings(final ResourceLoader res, final RuinsOfRevenge game) {
		super(new Stage(), game);
//		this.res = res;
//		this.game = game;
		this.gamepad = getController(Config.get().gamepad);

		Skin skin = res.getSkin("uiskin");

		final CheckBox bloomSwitch = new CheckBox(" Enable Bloom", skin);
		final CheckBox enableGamepad = new CheckBox(" Enable Gamepad", skin);
		final TextButton applyAndSave = new TextButton("Apply and Save", skin);
		final TextButton back = new TextButton("Back", skin);
		bloomSwitch.setChecked(Config.get().bloom);
		bloomSwitch.addListener(new TouchUpListener() {
			@Override
			public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
				Config.get().bloom = bloomSwitch.isChecked();
			}
		});
		enableGamepad.setChecked(Config.get().enableGamepad);
		enableGamepad.addListener(new TouchUpListener() {
			@Override
			public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
				Config.get().enableGamepad = enableGamepad.isChecked();
			}
		});
		applyAndSave.addListener(new TouchUpListener() {
			@Override
			public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
				Config.get().write();
			}
		});
		back.addListener(new TouchUpListener() {
			@Override
			public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
				game.popScreen();
			}
		});

		table = new Table(skin);
		table.add("Graphics").space(8).colspan(2);
		table.row();
		table.add("Bloom").space(8);
		table.add(bloomSwitch).space(8);
		table.row();
		table.add("Keys").space(8).colspan(2);
		table.row();
		for (ObjectMap.Entry<String, Integer> entry : Config.get().keys.entries()) {
			// Text on the left:
			table.add(entry.key).align(BaseTableLayout.LEFT).space(8);
			// Button on the right:
			TextButton button = new TextButton(KeysUtil.forVal(entry.value), skin, "toggle");
			button.addListener(new KeyInputListener(entry.key, button));
			table.add(button).width(128).space(8);
			table.row();
		}
		gamepadButton = new TextButton(Config.get().gamepad, skin, "toggle");
		gamepadX = new TextButton("Record", skin, "toggle");
		gamepadY = new TextButton("Record", skin, "toggle");
		gamepadButton.addListener(new TouchUpListener() {
			@Override
			public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
				gamepadRead = gamepadButton.isChecked();
			}
		});
		gamepadX.addListener(new TouchUpListener() {
			@Override
			public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
				gamepadXAxisRead = gamepadX.isChecked();
				gamepadX.setText(gamepadX.isChecked() ? "Recording..." : "Record");
			}
		});
		gamepadY.addListener(new TouchUpListener() {
			@Override
			public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
				gamepadYAxisRead = gamepadY.isChecked();
				gamepadY.setText(gamepadY.isChecked() ? "Recording..." : "Record");
			}
		});
		table.add("Gamepad").space(8).colspan(2);
		table.row();
		table.add("Enable").space(8);
		table.add(enableGamepad).space(8);
		table.row();
		table.add("Gamepad:").align(BaseTableLayout.LEFT).space(8);
		table.add(gamepadButton).width(128).space(8);
		table.row();
		table.add("Gamepad x axis:").align(BaseTableLayout.LEFT).space(8);
		table.add(gamepadX).width(128).space(8);
		table.row();
		table.add("Gamepad y axis:").align(BaseTableLayout.LEFT).space(8);
		table.add(gamepadY).width(128).space(8);
		table.row();
		table.add(back).size(160, 64).space(8);
		table.add(applyAndSave).size(160, 64).space(8);
		table.row();
		table.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);

		stage.addActor(table);
	}

	private Controller getController(String name) {
		for (Controller controller : Controllers.getControllers()) {
			if (controller.getName().equalsIgnoreCase(name)) {
				return controller;
			}
		}
		return null;
	}

	@Override
	public void tick(float delta) {
		stage.act(delta);
		if (gamepadRead) {
			search: for (Controller pad : Controllers.getControllers()) {
				for (int i = 0; i < 4; i++) {
					if (!FloatUtils.equalsEpsilon(pad.getAxis(i), 0, 0.1f)) {
						Config.get().gamepad = pad.getName();
						gamepadButton.setText(Config.get().gamepad);
						gamepadButton.setChecked(false);
						gamepadRead = false;
						break search;
					}
				}
			}
		}
		if (gamepadButton != null) {
			if (gamepadXAxisRead) {
				for (int i = 0; i < 4; i++) {
					if (!FloatUtils.equalsEpsilon(gamepad.getAxis(i), 0f, 0.5f)) {
						Config.get().gamepadX = i;
						gamepadXAxisRead = false;
						gamepadX.setChecked(false);
						gamepadX.setText("Record");
						break;
					}
				}
			}
			if (gamepadYAxisRead) {
				for (int i = 0; i < 4; i++) {
					if (!FloatUtils.equalsEpsilon(gamepad.getAxis(i), 0f, 0.5f)) {
						Config.get().gamepadY = i;
						gamepadYAxisRead = false;
						gamepadY.setChecked(false);
						gamepadY.setText("Record");
						break;
					}
				}
			}
		}
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
