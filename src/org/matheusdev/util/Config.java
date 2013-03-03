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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

/**
 * @author matheusdev
 *
 */
public class Config {

	public static final String configfile = "rorconfig.json";

	public static Config instance;

	public static Config get() {
		if (instance == null) {
			instance = read();
		}
		return instance;
	}

	public int resolutionX = 800;
	public int resolutionY = 600;

	public boolean bloom = true;

	private Config() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				get().write();
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

	public static Config read() {
		try {
			return new Json().fromJson(Config.class, new FileReader(new File(configfile)));
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
