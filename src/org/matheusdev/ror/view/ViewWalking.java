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
package org.matheusdev.ror.view;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.controller.component.AnimationHelper;
import org.matheusdev.ror.model.entity.Entity;
import org.matheusdev.util.Dir;
import org.matheusdev.util.JsonDOM;
import org.matheusdev.util.JsonDOM.JsonArray;
import org.matheusdev.util.JsonDOM.JsonElement;
import org.matheusdev.util.JsonDOM.JsonObject;
import org.matheusdev.util.MissingJSONContentException;
import org.matheusdev.util.SpriteAnimation;

/**
 * @author matheusdev
 *
 */
public class ViewWalking extends EntityView {

	public static final String name = "ViewWalking";

	private final Sprite sprite;
	private final float width;
	private final float xoffset;
	private final float yoffset;
    private final AnimationHelper anims;

    private int direction;
    private boolean moving;

	public ViewWalking(ResourceLoader res, JsonDOM.JsonObject jsonData) {
		this.width = Float.parseFloat(jsonData.values.get("width"));
		this.xoffset = Float.parseFloat(jsonData.values.get("xoffset"));
		this.yoffset = Float.parseFloat(jsonData.values.get("yoffset"));
        this.anims = new AnimationHelper();

        anims.set(tryReadAnimations(res, jsonData));
		this.sprite = new Sprite(anims.getKeyframe());
	}

    private SpriteAnimation[] tryReadAnimations(ResourceLoader res, JsonObject jsonData) {
        if (jsonData.elements.get("animations") instanceof JsonDOM.JsonArray) {
            return readAnimations(res, (JsonDOM.JsonArray) jsonData.elements.get("animations"));
        } else {
            throw new MissingJSONContentException("Array-tag \"animations: [ ... ]\" missing");
        }
    }

	private SpriteAnimation[] readAnimations(ResourceLoader res, JsonArray jsonArray) {
		SpriteAnimation[] anims = new SpriteAnimation[4];
		if (jsonArray.elements.size() != 4)
			throw new MissingJSONContentException("Got not enough or too many animations in \"view-conf: animations: [ ... ]\n tag: " + jsonArray.elements.size());
		for (JsonElement jsonElem : jsonArray.elements) {
			if (jsonElem instanceof JsonObject) {
				JsonObject jsonObj = (JsonObject) jsonElem;

				if (jsonObj.values.containsKey("up"))
					anims[Dir.UP   ] = res.getAnimation(jsonObj.values.get("up"));
				else if (jsonObj.values.containsKey("down"))
					anims[Dir.DOWN ] = res.getAnimation(jsonObj.values.get("down"));
				else if (jsonObj.values.containsKey("left"))
					anims[Dir.LEFT ] = res.getAnimation(jsonObj.values.get("left"));
				else if (jsonObj.values.containsKey("right"))
					anims[Dir.RIGHT] = res.getAnimation(jsonObj.values.get("right"));
			}
		}
		for (SpriteAnimation anim : anims) {
			if (anim == null)
				throw new MissingJSONContentException("Missing either left/right/up/down key in \"animations: [ ... ]\" tag");
		}
		return anims;
	}

	@Override
	public void draw(SpriteBatch batch, Entity e, float delta) {
        Vector2 linVel = e.getBody().getLinearVelocity();

        if (linVel.len() > 0.3f)
            direction = Dir.getDir(linVel.x, linVel.y);
        moving = linVel.len() > 0.1f;

        anims.setDirection(direction);
        anims.setMoving(moving);
        anims.setDeltaSpeed(delta);
        anims.apply(e);

		sprite.setRegion(anims.getKeyframe());
		draw(batch, e, sprite, width, xoffset, yoffset);
	}

}
