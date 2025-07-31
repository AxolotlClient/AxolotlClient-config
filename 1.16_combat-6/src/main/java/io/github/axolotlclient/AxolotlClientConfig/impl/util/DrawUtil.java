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

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Rectangle;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGFont;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class DrawUtil implements DrawingUtil {

	private static final DrawUtil INSTANCE = new DrawUtil();

	private static final Stack<Rectangle> scissorStack = new Stack<>();

	public static void fillRect(MatrixStack stack, Rectangle rectangle, Color color) {
		fillRect(stack, rectangle.x(), rectangle.y(), rectangle.width(),
			rectangle.height(),
			color.get().toInt());
	}

	public static void fillRect(MatrixStack graphics, int x, int y, int width, int height, int color) {
		DrawableHelper.fill(graphics, x, y, x + width, y + height, color);
	}

	public static void outlineRect(MatrixStack stack, Rectangle rectangle, Color color) {
		outlineRect(stack, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color.get().toInt());
	}

	public static void outlineRect(MatrixStack stack, int x, int y, int width, int height, int color) {
		fillRect(stack, x, y, 1, height - 1, color);
		fillRect(stack, x + width - 1, y + 1, 1, height - 1, color);
		fillRect(stack, x + 1, y, width - 1, 1, color);
		fillRect(stack, x, y + height - 1, width - 1, 1, color);
	}

	public static void drawCenteredString(MatrixStack stack, TextRenderer renderer,
										  String text, int centerX, int y,
										  int color, boolean shadow) {
		drawString(stack, renderer, text, centerX - renderer.getWidth(text) / 2,
			y,
			color, shadow);
	}

	public static void drawString(MatrixStack stack, TextRenderer renderer, String text, int x, int y,
								  int color, boolean shadow) {
		if (!shadow) {
			renderer.draw(stack, text, x, y, color);
		} else {
			renderer.drawWithShadow(stack, text, x, y, color);
		}
	}

	public static int nvgCreateImage(long ctx, Identifier texture) {
		return nvgCreateImage(ctx, texture, 0);
	}

	public static int nvgCreateImage(long ctx, Identifier texture, int imageFlags) {
		try {
			ByteBuffer buffer = mallocAndRead(MinecraftClient.getInstance().getResourceManager().getResource(texture)
				.getInputStream());
			int handle = NanoVG.nvgCreateImageMem(ctx, imageFlags, buffer);
			MemoryUtil.memFree(buffer);
			return handle;
		} catch (IOException ignored) {
		}
		return 0;
	}

	private static ByteBuffer mallocAndRead(InputStream in) throws IOException {
		try (ReadableByteChannel channel = Channels.newChannel(in)) {
			ByteBuffer buffer = MemoryUtil.memAlloc(8192);

			while (channel.read(buffer) != -1)
				if (buffer.remaining() == 0)
					buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() + buffer.capacity() * 3 / 2);

			((Buffer) buffer).flip();

			return buffer;
		}
	}

	public static void pushScissor(int x, int y, int width, int height) {
		pushScissor(new Rectangle(x, y, width, height));
	}

	public static void pushScissor(Rectangle rect) {
		if (!scissorStack.isEmpty()) {
			rect = intersection(rect, scissorStack.peek());
		}
		setScissor(scissorStack.push(rect));
	}

	private static Rectangle intersection(Rectangle a, Rectangle b) {
		int x = Math.max(a.x(), b.x());
		int y = Math.max(a.y(), b.y());
		int right = Math.min(a.x() + a.width(), b.x() + b.width());
		int bottom = Math.min(a.y() + a.height(), b.y() + b.height());
		return x < right && y < bottom ? new Rectangle(x, y, right - x, bottom - y) : new Rectangle(0, 0, 0, 0);
	}

	public static void popScissor() {
		scissorStack.pop();
		setScissor(scissorStack.empty() ? null : scissorStack.peek());
	}

	private static void setScissor(Rectangle rect) {
		if (rect != null) {
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			Window window = MinecraftClient.getInstance().getWindow();
			int scale = (int) window.getScaleFactor();
			GL11.glScissor(rect.x() * scale, (window.getScaledHeight() - rect.height() - rect.y()) * scale,
				rect.width() * scale, rect.height() * scale);
		} else {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}

	public static void drawScrollingText(MatrixStack stack, Text text, int x, int y, int width, int height, Color color) {
		drawScrollingText(stack, text.getString(), x, y, width, height, color);
	}

	public static void drawScrollingText(MatrixStack stack, String text, int x, int y, int width, int height, Color color) {
		drawScrollingText(stack, x, y, x + width, y + height, text, color);
	}

	public static void drawScrollingText(MatrixStack stack, int left, int top, int right, int bottom, String text, Color color) {
		drawScrollingText(stack, MinecraftClient.getInstance().textRenderer, text, (left + right) / 2, left, top, right, bottom, color);
	}

	public static void drawScrollingText(MatrixStack stack, TextRenderer renderer, String text, int center, int left, int top, int right, int bottom, Color color) {
		drawScrollingText(stack, renderer, text, center, left, top, right, bottom, color.toInt());
	}

	public static void drawScrollingText(MatrixStack stack, TextRenderer renderer, Text text, int center, int left, int top, int right, int bottom, int color) {
		int textWidth = renderer.getWidth(text);
		int y = (top + bottom - 9) / 2 + 1;
		int width = right - left;
		if (textWidth > width) {
			float r = textWidth - width;
			double d = (double) (System.nanoTime() / 1000000L) / 1000.0;
			double e = Math.max((double) r * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = f * r;
			pushScissor(left, top, right - left, bottom - top);
			renderer.drawWithShadow(stack, text, left - (int) g, y, color);
			popScissor();
		} else {
			int min = left + textWidth / 2;
			int max = right - textWidth / 2;
			int centerX = center < min ? min : Math.min(center, max);
			DrawableHelper.drawCenteredText(stack, renderer, text, centerX, y, color);
		}
	}

	public static void drawScrollingText(MatrixStack stack, TextRenderer renderer, String text, int center, int left, int top, int right, int bottom, int color) {
		int textWidth = renderer.getWidth(text);
		int y = (top + bottom - 9) / 2 + 1;
		int width = right - left;
		if (textWidth > width) {
			float r = textWidth - width;
			double d = (double) (System.nanoTime() / 1000000L) / 1000.0;
			double e = Math.max((double) r * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = f * r;
			pushScissor(left, top, right - left, bottom - top);
			drawString(stack, renderer, text, left - (int) g, y, color, true);
			popScissor();
		} else {
			int min = left + textWidth / 2;
			int max = right - textWidth / 2;
			int centerX = center < min ? min : Math.min(center, max);
			drawCenteredString(stack, renderer, text, centerX, y, color, true);
		}
	}

	public static void drawScrollingText(DrawingUtil drawingUtil, NVGFont font, Text text, int center, int left, int top, int right, int bottom, Color color) {
		float textWidth = font.getWidth(text.getString());
		int y = (top + bottom - 9) / 2 + 1;
		int width = right - left;
		if (textWidth > width) {
			float r = textWidth - width;
			double d = (double) Util.getMeasuringTimeMs() / 1000.0;
			double e = Math.max((double) r * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = MathHelper.lerp(f, 0.0, r);
			drawingUtil.pushScissor(NVGHolder.getContext(), left, top, right, bottom);
			drawingUtil.drawString(NVGHolder.getContext(), font, text.getString(), left - (int) g, y, color);
			drawingUtil.popScissor(NVGHolder.getContext());
		} else {
			float centerX = MathHelper.clamp(center, left + textWidth / 2, right - textWidth / 2);
			drawingUtil.drawCenteredString(NVGHolder.getContext(), font, text.getString(), centerX, y, color);
		}
	}

	public static void drawTooltip(MatrixStack graphics, Option<?> option, int x, int y) {
		String tooltip = I18n.translate(option.getTooltip());
		if (tooltip.equals(option.getTooltip())) {
			return;
		}
		String[] text = tooltip.split("<br>");
		if (!text[0].isEmpty() || text.length > 1) {
			MinecraftClient.getInstance().currentScreen.renderOrderedTooltip(graphics,
				Arrays.stream(text).map(Text::of)
					.flatMap((text1) -> MinecraftClient.getInstance().textRenderer.wrapLines(text1, 170).stream()).collect(Collectors.toList()), x, y);
		}
	}

	public static void drawTooltip(long ctx, NVGFont font, Option<?> option, int x, int y) {
		String tooltip = I18n.translate(option.getTooltip());
		if (tooltip.equals(option.getTooltip())) {
			return;
		}
		String[] text = tooltip.split("<br>");
		if (!text[0].isEmpty() || text.length > 1) {
			Screen screen = MinecraftClient.getInstance().currentScreen;
			// Uhhh hm?
			TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
			text = Arrays.stream(text).map(Text::of)
				.flatMap((text1) -> renderer.getTextHandler().wrapLines(text1, 170, Style.EMPTY).stream())
				.map(StringVisitable::getString)
				.toArray(String[]::new);
			INSTANCE.drawTooltip(ctx, font, text, x, y, screen.width, screen.height);
		}
	}

	public static String getFormattedString(StringVisitable component) {
		StringBuilder builder = new StringBuilder();
		component.visit((style, string) -> {
			if (style.getColor() != null && !style.getColor().getName().contains("#")) {
				builder.append(Formatting.byName(style.getColor().getName()));
			}
			if (style.isBold()) {
				builder.append(Formatting.BOLD);
			}
			if (style.isUnderlined()) {
				builder.append(Formatting.UNDERLINE);
			}
			if (style.isObfuscated()) {
				builder.append(Formatting.OBFUSCATED);
			}
			if (style.isItalic()) {
				builder.append(Formatting.ITALIC);
			}
			if (style.isStrikethrough()) {
				builder.append(Formatting.STRIKETHROUGH);
			}
			builder.append(string);
			builder.append(Formatting.RESET);
			return Optional.empty();
		}, Style.EMPTY);
		return builder.toString();
	}
}
