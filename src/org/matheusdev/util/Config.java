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

    public OrderedMap<String, Integer> keys = new OrderedMap<>();
    {
        keys.put("up", Input.Keys.W);
        keys.put("down", Input.Keys.S);
        keys.put("left", Input.Keys.A);
        keys.put("right", Input.Keys.D);
        keys.put("debugDraw", Input.Keys.F8);
        keys.put("escape", Input.Keys.ESCAPE);
        keys.put("chat", Input.Keys.ENTER);
    }
	public int resolutionX = 800;
	public int resolutionY = 600;
	public boolean enableGamepad = true;
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

	public int key(String name) {
        Integer key = keys.get(name);
        if (key != null) return key;
		throw new InvalidConfigException("Missing key in configuration File (keyconfig.json): " + name);
	}

	public void key(String name, int value) {
        System.out.println("Putting key " + name + " = " + KeysUtil.forVal(value));
        keys.put(name, value);
	}

	public static Config read() throws IOException {
        System.out.println("Reading config.");
        File config = new File(configfile);

        if (config.exists()) {
            JsonDOM dom = new Json().fromJson(JsonDOM.class, new FileReader(config));

            if (dom != null) return new Config().fromJsonDOM(dom);
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
			writer.write(json.prettyPrint(toJsonDOM(), 100));
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

    public Config fromJsonDOM(JsonDOM dom) {
        JsonDOM.JsonObject root = dom.getRoot();
        gamepad = root.getValue("gamepad", "...");

        gamepadX = root.getIntValue("gamepadX", 0);
        gamepadY = root.getIntValue("gamepadY", 1);

        enableGamepad = root.getBoolValue("enableGamepad", false);
        
        baseintensity = root.getFloatValue("baseintensity", 1f);
        basesaturation = root.getFloatValue("basesaturation", 0.85f);
        bloomintensity = root.getFloatValue("bloomintensity", 2f);
        bloomsaturation = root.getFloatValue("bloomsaturation", 0.85f);
        bloomtreshold = root.getFloatValue("bloomtreshold", 0.6f);
        blurammount = root.getFloatValue("blurammount", 2f);
        
        resolutionX = root.getIntValue("resolutionX", 800);
        resolutionY = root.getIntValue("resolutionY", 600);

        bloom = root.getBoolValue("bloom", true);

        if (root.elements.containsKey("keys")) {
            JsonDOM.JsonElement keysElem = root.elements.get("keys");

            if (keysElem != null && keysElem instanceof JsonDOM.JsonArray) {
                JsonDOM.JsonArray keyArray = (JsonDOM.JsonArray) keysElem;

                for (JsonDOM.JsonObject obj : keyArray.elements) {
                    for (Entry<String, String> key : obj.values.entries()) {
                        keys.put(key.key, KeysUtil.forName(key.value));
                    }
                }
            }
        }
        return this;
    }

    public JsonDOM toJsonDOM() {
        JsonDOM dom = new JsonDOM();
        JsonDOM.JsonObject root = dom.getRoot();
        root.values.put("gamepad", gamepad);
        root.values.put("gamepadX", "" + gamepadX);
        root.values.put("gamepadY", "" + gamepadY);
        root.values.put("enableGamepad", "" + enableGamepad);
        root.values.put("baseintensity", "" + baseintensity);
        root.values.put("basesaturation", "" + basesaturation);
        root.values.put("bloomintensity", "" + bloomintensity);
        root.values.put("bloomsaturation", "" + bloomsaturation);
        root.values.put("bloomtreshold", "" + bloomtreshold);
        root.values.put("blurammount", "" + blurammount);
        root.values.put("resolutionX", "" + resolutionX);
        root.values.put("resolutionY", "" + resolutionY);
        root.values.put("bloom", "" + bloom);

        JsonDOM.JsonArray keyArray = new JsonDOM.JsonArray();
        for (Entry<String, Integer> entry : keys.entries()) {
            JsonDOM.JsonObject obj = new JsonDOM.JsonObject();
            obj.values.put(entry.key, KeysUtil.forVal(entry.value));
            System.out.println("Saving " + entry.key + " = " + KeysUtil.forVal(entry.value));
            keyArray.elements.add(obj);
        }
        root.elements.put("keys", keyArray);

        return dom;
    }

}
