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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;

import java.io.*;

/**
 * @author matheusdev
 *
 */
public class Config {

	public static final class Key implements Json.Serializable {
		public String name;
		public int value;

        public Key() {}

		public Key(String name, int value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void write(Json json) {
			json.writeValue(name, KeysUtil.forVal(value));
		}

		@Override
		public void read(Json json, OrderedMap<String, Object> jsonData) {
			Entry<String, Object> entry = jsonData.entries().next();
			name = entry.key;
			try {
				value = (int)Float.parseFloat(entry.value.toString());
			} catch (NumberFormatException e) {
				value = KeysUtil.forName(entry.value.toString());
			}
		}
	}

	public static final String configfile = "keyconfig.json";

	public static Config instance;

	public static Config get() {
		if (instance == null) {
            try {
                instance = read();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		return instance;
	}

	public Array<Key> keys = new Array<>(Key.class);
    {
        keys.add(new Key("up", Input.Keys.W));
        keys.add(new Key("down", Input.Keys.W));
        keys.add(new Key("left", Input.Keys.W));
        keys.add(new Key("right", Input.Keys.W));
        keys.add(new Key("debugDraw", Input.Keys.W));
        keys.add(new Key("escape", Input.Keys.ESCAPE));
    }
	public int resolutionX = 800;
	public int resolutionY = 600;
	public boolean enableGamepad;
	public String gamepad = "...";
	public int gamepadX = 99;
	public int gamepadY = 100;
	public boolean bloom = true;
	public float baseintensity = 1f;
	public float basesaturation = .85f;
	public float bloomtreshold = 0.6f;//0.577f;
	public float bloomintensity = 2.0f;
	public float bloomsaturation = .85f;
	public float blurammount = 2f;

	private Config() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				get().write();
				System.out.println("Config written.");
			}
		}));
	}

	public Config setRes(int resX, int resY) {
		resolutionX = resX;
		resolutionY = resY;
		return this;
	}

	public Config setBloom(boolean bloom) {
		this.bloom = bloom;
		return this;
	}

	public int key(String name) {
		for (Key key : keys) {
			if (name.equalsIgnoreCase(key.name))
				return key.value;
		}
		throw new InvalidConfigException("Missing key in configuration File (keyconfig.json): " + name);
	}

	public void key(String name, int value) {
		for (Key key : keys) {
			if (name.equalsIgnoreCase(key.name)) {
				key.value = value;
				return;
			}
		}
		keys.add(new Key(name, value));
	}

	public static Config read() throws IOException {
		try {
			System.out.println("Reading config.");
            File config = new File(configfile);

			Config conf = new Json().fromJson(Config.class, new FileReader(config));

            if (conf != null) return conf;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new Config();
	}

	public void write() {
		FileWriter writer = null;
		try {
			Json json = new Json();
			json.setIgnoreUnknownFields(false);
			json.setUsePrototypes(false);
			json.setOutputType(OutputType.javascript);
			writer = new FileWriter(new File(configfile));
			writer.write(json.prettyPrint(this, 100));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
