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

import org.matheusdev.ror.screens.ScreenMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL10;

/**
 * @author matheusdev
 *
 */
public class RuinsOfRevenge extends Game {

	public static void main(String[] args) {
		new LwjglApplication(new RuinsOfRevenge(), "matheusdev: Ruins Of Revenge", 800, 600, true);
	}

	private ResourceLoader res;

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	@Override
	public void create() {
		try {
			res = new ResourceLoader(Gdx.files.internal("data/sprites/resources.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
//		FileHandle testMapTmx = Gdx.files.internal("data/maps/newmap/map004.tmx");
//		ScreenGameMap screen = new ScreenGameMap(testMapTmx, res);
		setScreen(new ScreenMenu(this, res));
		Gdx.graphics.setVSync(true);
	}

	@Override
	public void render() {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.gl.glViewport(0, 0, width, height);
		super.resize(width, height);
	}

	@Override
	public void dispose() {
		super.dispose();
		getScreen().dispose();
		res.dispose();
	}

}
