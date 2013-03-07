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

/**
 * @author matheusdev
 *
 */
public final class Dir {

	public static class InvalidDirectionException extends RuntimeException {
		private static final long serialVersionUID = 6197289793612370308L;

		public InvalidDirectionException(int dir) {
			super("Not a valid direction: " + dir + ". Has to be in range 0-3");
		}
	}

	/**
	 * Don't use the constructor! I dare you!
	 */
	public Dir() {
		System.err.println("What did I say you in the Javadoc about the Dir() constructor?!");
		new Dir();
	}

	public static final int DOWN = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int UP = 3;

	public static String getName(int dir) {
		switch (dir) {
		case UP: return "up";
		case DOWN: return "down";
		case LEFT: return "left";
		case RIGHT: return "right";
		}
		return "unknown-direction";
	}

	public static int getDir(String name) {
		switch (name.toLowerCase()) {
		case "up": return UP;
		case "down": return DOWN;
		case "left": return LEFT;
		case "right": return RIGHT;
		}
		return -1;
	}

	public static boolean isValid(int dir) {
		if (dir >= 0 && dir < 4)  return true;
		return false;
	}

	public static void validCheck(int dir) {
		if (!isValid(dir)) throw new InvalidDirectionException(dir);
	}

}
