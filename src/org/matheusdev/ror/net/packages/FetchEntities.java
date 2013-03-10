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
package org.matheusdev.ror.net.packages;

/**
 * @author matheusdev
 *
 */
public class FetchEntities extends NetPackage {

	public CreateEntity[] creates;

	public FetchEntities() {
	}

	public FetchEntities(long time, CreateEntity... creates) {
		super(time);
		this.creates = creates;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("[FetchEntities]: [\n");

		if (creates != null) {
			for (CreateEntity create : creates) {
				builder.append("\t{\n");
				if (create != null)
					builder.append(create);
				else
					builder.append("null");
				builder.append("\n\t},\n");
			}
		}

		builder.append("]");

		return builder.toString();
	}

}
