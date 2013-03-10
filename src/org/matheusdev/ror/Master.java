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

import java.io.File;

import org.matheusdev.util.JsonDOM;
import org.matheusdev.util.JsonDOM.JsonObject;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author matheusdev
 *
 */
public class Master {

	private final ObjectMap<String, JsonObject> entityTypePool = new ObjectMap<>();
	private final String basePath;

	public Master(String entityBasePath) {
		this.basePath = entityBasePath;
	}

	protected JsonObject getEntityJson(String type) {
		JsonObject json = entityTypePool.get(type);
		if (json == null) {
			try {
				JsonDOM dom = new Json().fromJson(JsonDOM.class, new FileHandle(new File(basePath + type + ".json")));
				json = dom.getRoot();
				entityTypePool.put(type, json);
			} catch (Exception ex) {
				System.err.println(new UnkownEntityTypeException("Couldn't load entity named \"" + type + "\" from " + basePath + type + ".json: " + ex));
				throw ex;
			}
		}
		return json;
	}

}
