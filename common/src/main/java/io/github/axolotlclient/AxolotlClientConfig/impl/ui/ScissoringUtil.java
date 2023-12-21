/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.util.Stack;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Rectangle;
import lombok.AccessLevel;
import lombok.Getter;

import static org.lwjgl.nanovg.NanoVG.*;

public class ScissoringUtil {


	@Getter(AccessLevel.PACKAGE)
	private static final ScissoringUtil instance = new ScissoringUtil();

	private final Stack<Rectangle> scissorStack = new Stack<>();

	public void push(long ctx, int x, int y, int width, int height) {
		set(ctx, scissorStack.push(new Rectangle(x, y, width, height)));
	}

	private void set(long ctx, Rectangle rectangle) {
		if (rectangle != null) {
			if (scissorStack.size() > 1) {
				nvgIntersectScissor(ctx, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
			} else {
				nvgScissor(ctx, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
			}
		} else {
			nvgResetScissor(ctx);
		}
	}

	public void pop(long ctx) {
		scissorStack.pop();
		set(ctx, scissorStack.empty() ? null : scissorStack.peek());
	}
}
