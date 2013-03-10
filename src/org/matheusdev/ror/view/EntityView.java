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

import org.matheusdev.ror.client.ClientEntity;
import org.matheusdev.ror.model.entity.Entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * @author matheusdev
 *
 */
public abstract class EntityView {

	public abstract void draw(SpriteBatch batch, ClientEntity e, float delta);

	public void draw(SpriteBatch batch, Entity e, Sprite sprite, float width, float xoffset, float yoffset) {
		Body body = e.getBody();
		// Super-Duper important Box2D-Magic code:
		// This should be / is in almost every Box2D project
		// It takes the Body and the associated sprite and
		// renders the sprite properly, using the body's
		// position, rotation and origin.
		final float worldToSprite = sprite.getWidth() / width;
		final float spriteToWorld = width / sprite.getWidth();
		// Get body position:
		final float bodyX = body.getPosition().x;
		final float bodyY = body.getPosition().y;
		// Get body center:
		final Vector2 center = body.getLocalCenter();
		final Vector2 massCenter = body.getMassData().center;
		center.sub(massCenter).add(xoffset, yoffset);
		// Compute sprite-space center:
		final Vector2 spriteCenter = new Vector2(sprite.getWidth()/2, sprite.getHeight()/2).sub((center.cpy().mul(worldToSprite)));
		// Upload to sprite:
		sprite.setScale(1f * spriteToWorld);
		sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
		sprite.setOrigin(spriteCenter.x, spriteCenter.y);
		sprite.setPosition(
				bodyX - spriteCenter.x,
				bodyY - spriteCenter.y);
		// Draw Sprite:
		sprite.draw(batch);
	}

}
