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

package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import java.util.Arrays;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

public class DrawUtil {

	public static void fillRect(GuiGraphics stack, Rectangle rectangle, Color color) {
		fillRect(stack, rectangle.x(), rectangle.y(), rectangle.width(),
			rectangle.height(),
			color.get().toInt());
	}

	public static void fillRect(GuiGraphics graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + height, color);
	}

	public static void outlineRect(GuiGraphics stack, Rectangle rectangle, Color color) {
		outlineRect(stack, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color.get().toInt());
	}

	public static void outlineRect(GuiGraphics stack, int x, int y, int width, int height, int color) {
		fillRect(stack, x, y, 1, height - 1, color);
		fillRect(stack, x + width - 1, y + 1, 1, height - 1, color);
		fillRect(stack, x + 1, y, width - 1, 1, color);
		fillRect(stack, x, y + height - 1, width - 1, 1, color);
	}

	public static void drawCenteredString(GuiGraphics stack, TextRenderer renderer,
										  String text, int centerX, int y,
										  int color, boolean shadow) {
		drawString(stack, renderer, text, centerX - renderer.getWidth(text) / 2,
			y,
			color, shadow);
	}

	public static void drawString(GuiGraphics stack, TextRenderer renderer, String text, int x, int y,
								  int color, boolean shadow) {
		stack.drawText(renderer, text, x, y, color, shadow);
	}

	public static void drawScrollingText(GuiGraphics stack, Text text, int x, int y, int width, int height, Color color) {
		drawScrollingText(stack, text.getString(), x, y, width, height, color);
	}

	public static void drawScrollingText(GuiGraphics stack, String text, int x, int y, int width, int height, Color color) {
		drawScrollingText(stack, x, y, x + width, y + height, text, color);
	}

	public static void drawScrollingText(GuiGraphics stack, int left, int top, int right, int bottom, String text, Color color) {
		drawScrollingText(stack, MinecraftClient.getInstance().textRenderer, text, (left + right) / 2, left, top, right, bottom, color);
	}

	public static void drawScrollingText(GuiGraphics graphics, TextRenderer renderer, String text, int center, int left, int top, int right, int bottom, Color color) {
		int textWidth = renderer.getWidth(text);
		int y = (top + bottom - 9) / 2 + 1;
		int width = right - left;
		if (textWidth > width) {
			float r = textWidth - width;
			double d = (double) (System.nanoTime() / 1000000L) / 1000.0;
			double e = Math.max((double) r * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = f * r;
			graphics.enableScissor(left, top, right, bottom);
			drawString(graphics, renderer, text, left - (int) g, y, color.toInt(), true);
			graphics.disableScissor();
		} else {
			int min = left + textWidth / 2;
			int max = right - textWidth / 2;
			int centerX = center < min ? min : Math.min(center, max);
			drawCenteredString(graphics, renderer, text, centerX, y, color.toInt(), true);
		}
	}

	public static void drawTooltip(GuiGraphics graphics, Option<?> option, int x, int y) {
		String tooltip = I18n.translate(option.getTooltip());
		if (tooltip.equals(option.getTooltip())) {
			return;
		}
		String[] text = tooltip.split("<br>");
		if (!text[0].isEmpty() || text.length > 1) {
			TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
			graphics.drawTooltip(renderer,
				Arrays.stream(text).map(Text::of)
					.flatMap((text1) -> renderer.wrapLines(text1, 170).stream()).toList(), DefaultTooltipPositioner.INSTANCE, x - 2, y + 12 + 3 + 10);
		}
	}
}
