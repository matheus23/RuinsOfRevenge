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
import com.badlogic.gdx.utils.XmlReader.Element;


/**
 * @author matheusdev
 *
 */
public class SpriteAnimation {

	private final Texture tex;
	private final float[] delays;
	private final float totalDelays;
	private final TextureRegion[] keyframes;
	private final String name;

	private float time;
	private int frame;

	public SpriteAnimation(Texture texture, Element elem) throws RuntimeException {
		this.tex = texture;
		this.name = elem.get("name", "noname");
		int frames = elem.getChildCount();

		if (frames == 0)
			throw new RuntimeException("animation is empty");

		delays = new float[frames];
		keyframes = new TextureRegion[frames];

		if (elem.getAttributes().containsKey("delay")) {
			setDelay(elem.getFloat("delay"));
		}

		for (int i = 0; i < elem.getChildCount(); i++) {
			Element child = elem.getChild(i);

			keyframes[i] = XmlUtils.getTexReg(tex, child.getAttribute("bounds"));

			if (child.getAttributes().containsKey("delay")) {
				delays[i] = child.getFloat("delay");
			}
		}
		float totalDelay = 0;
		for (float delay : delays) {
			totalDelay += delay;
		}
		totalDelays = totalDelay;
	}

	public void setDelay(float delay) {
		for (int i = 0; i < delays.length; i++) {
			delays[i] = delay;
		}
	}

	public String getName() {
		return name;
	}

	public int getCurrentFrame() {
		float lookupTime = time % totalDelays;
		float visitedTime = 0;

		for (int i = 0; i < delays.length-1; i++) {
			visitedTime += delays[i];
			if (lookupTime < visitedTime) {
				return i;
			}
		}
		return 0;
	}

	public void tick(float delta) {
		time += delta;
		frame = getCurrentFrame();
	}

	public TextureRegion getCurrentKeyframe() {
		return keyframes[frame];
	}

}
