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

import org.matheusdev.util.SpriteAnimation;
import org.matheusdev.util.XmlUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author matheusdev
 *
 */
public class ResourceLoader implements Disposable {

	private boolean disposed;

	private final ObjectMap<String, TextureRegion> regions;
	private final ObjectMap<String, SpriteAnimation> anims;

	public ResourceLoader(FileHandle resourceXml) throws IOException {
		regions = new ObjectMap<>();
		anims = new ObjectMap<>();

		Element resources = new XmlReader().parse(resourceXml);
		readResourcesTag(resources);
	}

	/// RUNTIME API:
	/// ===========

	public TextureRegion getRegion(String name) {
		return regions.get(name);
	}

	public SpriteAnimation getAnimation(String name) {
		return anims.get(name);
	}

	/// RESOURCE LOADING:
	/// ================

	private void readResourcesTag(Element resources) throws RuntimeException {
		for (int i = 0; i < resources.getChildCount(); i++) {
			Element child = resources.getChild(i);

			switch (child.getName()) {
			case "images":
				readImagesTag(child);
				break;
			default:
				throw new RuntimeException("Expected <images> tag");
			}
		}
	}

	private void readImagesTag(Element images) throws RuntimeException {
		for (int i = 0; i < images.getChildCount(); i++) {
			Element child = images.getChild(i);

			switch (child.getName()) {
			case "image":
				readImageTag(child);
				break;
			default:
				throw new RuntimeException("Expected <image> tag");
			}
		}
	}

	private void readImageTag(Element image) throws RuntimeException {
		if (!image.getAttributes().containsKey("file"))
			throw new RuntimeException("need file=\"...\" attribute");

		Texture tex = new Texture(Gdx.files.internal(image.get("file")));

		for (int i = 0; i < image.getChildCount(); i++) {
			Element child = image.getChild(i);

			switch (child.getName()) {
			case "region":
				readRegionTag(tex, child);
				break;
			case "animation":
				readAnimationTag(tex, child);
				break;
			default:
				throw new RuntimeException("Expected <region> or <animation> tag");
			}
		}
	}

	private void readRegionTag(Texture tex, Element region) throws RuntimeException {
		if (!region.getAttributes().containsKey("name")
				|| ! region.getAttributes().containsKey("bounds"))
			throw new RuntimeException("need name=\"...\" and bounds=\"...\" property");
		regions.put(region.get("name"), XmlUtils.getTexReg(tex, region.get("bounds")));
	}

	private void readAnimationTag(Texture tex, Element anim) throws RuntimeException {
		if (!anim.getAttributes().containsKey("name"))
			throw new RuntimeException("need name=\"...\" property");
		anims.put(anim.get("name"), new SpriteAnimation(tex, anim));
	}

	public void dispose() {
		if (!disposed) {
			disposed = true;
			for (Entry<String, TextureRegion> entry : regions.entries()) {
				entry.value.getTexture().dispose();
			}
			for (Entry<String, SpriteAnimation> entry : anims.entries()) {
				entry.value.dispose();
			}
		}
	}

}
