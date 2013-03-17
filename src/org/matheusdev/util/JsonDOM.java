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
package org.matheusdev.util;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;

import java.util.ArrayList;

/**
 * @author matheusdev
 *
 */
public class JsonDOM implements Serializable {

	public static abstract class JsonElement {
		public abstract void toString(StringBuffer buffer);
	}

	public static class JsonArray extends JsonElement {
		public ArrayList<JsonObject> elements = new ArrayList<>();
		@Override
		public void toString(StringBuffer buffer) {
			buffer.append("[\n");
			for (JsonElement e : elements) {
				e.toString(buffer);
			}
			buffer.append("]\n");
		}
	}

	public static class JsonObject extends JsonElement {
		public OrderedMap<String, JsonElement> elements = new OrderedMap<>();
		public OrderedMap<String, String> values = new OrderedMap<>();

		@Override
		public void toString(StringBuffer buffer) {
			buffer.append("{\n");
			for (Entry<String, String> entry : values.entries()) {
				buffer.append(entry.key).append(": ").append(entry.value).append('\n');
			}
			for (Entry<String, JsonElement> entry : elements.entries()) {
				buffer.append(entry.key).append(": ");
				entry.value.toString(buffer);
			}
			buffer.append("}\n");
		}

		public boolean valuesContainAllKeys(String... keys) {
			for (String key : keys) {
				if (!values.containsKey(key))
					return false;
			}
			return true;
		}
	}

	private final JsonObject root;

	public JsonDOM() {
		root = new JsonObject();
	}

	public JsonObject getRoot() {
		return root;
	}

	@Override
	public void write(Json json) {
	}

	@Override
	public void read(Json json, OrderedMap<String, Object> jsonData) {
		handleJsonObject(root, jsonData);
	}

	@SuppressWarnings("unchecked")
	public void handleJsonObject(JsonObject element, OrderedMap<String, Object> jsonData) {
		Entries<String, Object> entries = jsonData.entries();
		for (Entry<String, Object> entry : entries) {
			if (entry.value instanceof OrderedMap) {
				JsonObject obj = new JsonObject();
				element.elements.put(entry.key, obj);

				// unchecked, but safe:
				handleJsonObject(obj, (OrderedMap<String, Object>)entry.value);
			} else if (entry.value instanceof Array) {
				JsonArray arr = new JsonArray();
				element.elements.put(entry.key, arr);

				// unchecked, but safe:
				handleJsonArray(arr, (Array<OrderedMap<String, Object>>) entry.value);
			} else {
				element.values.put(entry.key, entry.value.toString());
			}
		}
	}

	public void handleJsonArray(JsonArray array, Array<OrderedMap<String, Object>> jsonArray) {
		for (OrderedMap<String, Object> jsonObject : jsonArray) {
			JsonObject obj = new JsonObject();
			array.elements.add(obj);
			handleJsonObject(obj, jsonObject);
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("{\n");
		root.toString(buffer);
		buffer.append("\n}");
		return buffer.toString();
	}

}
