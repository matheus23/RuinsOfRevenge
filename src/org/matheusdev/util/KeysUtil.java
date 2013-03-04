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

import java.lang.reflect.Field;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author matheusdev
 *
 */
public final class KeysUtil {

	private static final ObjectMap<String, Integer> keysVal = new ObjectMap<>();
	private static final ObjectMap<Integer, String> keysName = new ObjectMap<>();

	static {
		Field[] fields = Keys.class.getFields();
		for (Field field : fields) {
			String name = field.getName();
			if (!name.startsWith("META")) {
				try {
					int value = field.getInt(null);
					keysVal.put(name, value);
					keysName.put(value, name);
				} catch (Exception e) {
					System.err.println("Exception when loading Keys: " + e);
				}
			}
		}
	}

	public static void touch() {}

	public static final int forName(String name) {
		return keysVal.get(name);
	}

	public static final String forVal(int val) {
		return keysName.get(val);
	}

	/**
	 * Don't call the constructor! DONT'!
	 */
	private KeysUtil() {
		System.err.println("What did I tell you in the Javadoc?");
		new KeysUtil();
	}

}
