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

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

public interface DrawingUtil {

	default void pushScissor(long ctx, int x, int y, int width, int height) {
		ScissoringUtil.getInstance().push(ctx, x, y, width, height);
	}

	default void popScissor(long ctx) {
		ScissoringUtil.getInstance().pop(ctx);
	}

	default void fill(long ctx, float x, float y, float width, float height, Color color) {
		nvgBeginPath(ctx);
		nvgFillColor(ctx, color.toNVG());
		nvgRect(ctx, x, y, width, height);
		nvgFill(ctx);
	}

	default void fillRoundedRect(long ctx, float x, float y, float width, float height, Color color, float radius) {
		nvgBeginPath(ctx);
		nvgFillColor(ctx, color.toNVG());
		nvgRoundedRect(ctx, x, y, width, height, radius);
		nvgFill(ctx);
	}

	default void fillCircle(long ctx, float centerX, float centerY, Color color, float radius, float startDeg, float endDeg) {
		startDeg -= 90;
		endDeg -= 90;
		nvgBeginPath(ctx);
		nvgFillColor(ctx, color.toNVG());
		nvgArc(ctx, centerX, centerY, radius, nvgDegToRad(startDeg), nvgDegToRad(endDeg), NVG_CW);
		nvgFill(ctx);
	}

	default void outline(long ctx, float x, float y, float width, float height, Color color, float lineWidth) {
		nvgStrokeWidth(ctx, lineWidth);
		nvgBeginPath(ctx);
		nvgStrokeColor(ctx, color.toNVG());
		nvgMoveTo(ctx, x, y);
		nvgLineTo(ctx, x + width, y);
		nvgLineTo(ctx, x + width, y + height);
		nvgLineTo(ctx, x, y + height);
		nvgLineTo(ctx, x, y);

		nvgStroke(ctx);
	}

	default void outlineRoundedRect(long ctx, float x, float y, float width, float height, Color color, float radius, float lineWidth) {
		nvgBeginPath(ctx);
		nvgStrokeColor(ctx, color.toNVG());
		nvgRoundedRect(ctx, x, y, width, height, radius);
		nvgStrokeWidth(ctx, lineWidth);
		nvgStroke(ctx);
	}

	default void outlineCircle(long ctx, float centerX, float centerY, Color color, float radius, float lineWidth, float startDeg, float endDeg) {
		startDeg -= 90;
		endDeg -= 90;
		nvgBeginPath(ctx);
		nvgArc(ctx, centerX, centerY, radius, nvgDegToRad(startDeg), nvgDegToRad(endDeg), NVG_CW);
		nvgStrokeColor(ctx, color.toNVG());
		nvgStrokeWidth(ctx, lineWidth);
		nvgStroke(ctx);
	}

	default NVGColor toNVGColor(int color) {
		return toNVGColor(color >> 24 & 0xFF, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF);
	}

	default NVGColor toNVGColor(int alpha, int red, int green, int blue) {
		return nvgRGBA((byte) red, (byte) green, (byte) blue, (byte) alpha, NVGColor.create());
	}

	default float drawString(long ctx, NVGFont font, String text, float x, float y, Color color) {
		return drawStringWithFormatting(ctx, font, text, x, y, color);
	}

	default float drawStringWithFormatting(long ctx, NVGFont font, String text, float x, float y, Color color) {
		return FormattingUtil.getInstance().drawStringWithFormatting(ctx, font, text, x, y, color);
	}

	default float drawCenteredString(long ctx, NVGFont font, String text, float centerX, float y, Color color) {
		return drawString(ctx, font, text, centerX - font.getWidth(text) / 2, y, color);
	}

	default void drawScrollingText(long ctx, NVGFont font, String text, int x, int y, int width, int height, Color color) {
		int xOffset = 2;
		drawScrollingText(ctx, font, x + xOffset, y, x + width - xOffset, y + height, text, color);
	}

	default void drawScrollingText(long ctx, NVGFont font, int left, int top, int right, int bottom, String text, Color color) {
		drawScrollingText(ctx, font, text, (left + right) / 2, left, top, right, bottom, color);
	}

	default void drawScrollingText(long ctx, NVGFont font, String text, int center, int left, int top, int right, int bottom, Color color) {
		float textWidth = font.getWidth(text);
		float y = top + (bottom - top) / 2f - font.getLineHeight() * 2 / 3;
		int width = right - left;
		if (textWidth > width) {
			float r = textWidth - width;
			double d = (double) (System.nanoTime() / 1000000L) / 1000.0;
			double e = Math.max((double) r * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = f * r;
			pushScissor(ctx, left, top, right - left, bottom - top);
			drawString(ctx, font, text, left - (int) g, y, color);
			popScissor(ctx);
		} else {
			float min = left + textWidth / 2;
			float max = right - textWidth / 2;
			float centerX = center < min ? min : Math.min(center, max);
			drawCenteredString(ctx, font, text, centerX, y, color);
		}
	}

	default void drawTooltip(long ctx, NVGFont font, String[] tooltipText, int x, int y, int screenWidth, int screenHeight) {

		float lineHeight = font.getLineHeight();
		x += 5;
		y += 5;
		screenWidth-=5;
		screenHeight-=5;
		float maxWidth = 0;
		float height = lineHeight*tooltipText.length;
		for (String s : tooltipText) {
			maxWidth = Math.max(font.getWidth(s), maxWidth);
		}
		if (maxWidth > screenWidth - x) {
			x = (int) (screenWidth - maxWidth-4);
		}
		if (height > screenHeight - y) {
			y = (int) (screenHeight-height-5);
		}

		fillRoundedRect(ctx, x + 2, y + 2, maxWidth + 5, height + 6, Colors.foreground(), 5);

		float cY = y + 4;
		for (String s : tooltipText) {
			drawString(ctx, font, s, x + 4, cY, Colors.text());
			cY += lineHeight;
		}
	}
}
