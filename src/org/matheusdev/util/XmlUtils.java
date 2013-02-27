/*
 * Copyright (c) 2012 matheusdev
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
package org.matheusdev.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author matheusdev
 *
 */
public final class XmlUtils {

	/**
	 * Don't call this!
	 */
	public XmlUtils() {
		System.err.println("What did I write in the docs? ^^");
		new XmlUtils();
	}

	/**
	 * Returns a texture region using the String "str", which is
	 * for example "32 64 16 16" - in the form of "x y width height".
	 * @param tex the Texture to get the Region from.
	 * @param str the String defining the area.
	 */
	public static TextureRegion getTexReg(Texture tex, String str) {
		try {
			// TODO: Make more pretty;
			String[] strs = str.split(" ");
			if (strs.length != 4)
				throw new RuntimeException("Need x, y, width and height");
			int x = Integer.parseInt(strs[0]);
			int y = Integer.parseInt(strs[1]);
			int w = Integer.parseInt(strs[2]);
			int h = Integer.parseInt(strs[3]);

			return new TextureRegion(tex, x, y, w, h);
		} catch (Exception e) {
			System.err.println("Couldn't parse XML Resource: " + e);
			System.exit(1);
			return null;
		}
	}

}
