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
package org.matheusdev.ror.controller.component;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.util.Dir;
import org.matheusdev.util.SpriteAnimation;

/**
 * @author matheusdev
 *
 */
public class AnimationHelper extends Component {

	private SpriteAnimation[] animations;
	private boolean moving;
	private int direction;
	private float deltaSpeed;

	public void set(SpriteAnimation... animations) {
		if (animations.length != 4) {
			throw new IllegalArgumentException("Need four animations. Got " + animations.length + " instead.");
		}
		this.animations = animations;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public void setDirection(int direction) {
		Dir.validCheck(direction);
		this.direction = direction;
	}

	public void setDeltaSpeed(float deltaSpeed) {
		this.deltaSpeed = deltaSpeed;
	}

    public TextureRegion getKeyframe() {
        return animations[direction].getCurrentKeyframe();
    }

	@Override
	public void apply(Entity entity) {
		if (moving) {
			animations[direction].tick(deltaSpeed);
		} else {
			for (SpriteAnimation anim : animations) {
				anim.reset();
			}
		}
	}

}
