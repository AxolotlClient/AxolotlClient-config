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
import java.util.List;
import java.util.Stack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Rectangle;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.Identifier;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class DrawUtil extends GuiElement implements DrawingUtil {

	private static final DrawUtil INSTANCE = new DrawUtil();

	private static final Stack<Rectangle> scissorStack = new Stack<>();

	public static void fillRect(Rectangle rectangle, Color color) {
		fillRect(rectangle.x(), rectangle.y(), rectangle.width(),
			rectangle.height(),
			color.get().toInt());
	}

	public static void fillRect(int x, int y, int width, int height, int color) {
		GuiElement.fill(x, y, x + width, y + height, color);
	}

	public static void outlineRect(Rectangle rectangle, Color color) {
		outlineRect(rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color.get().toInt());
	}

	public static void outlineRect(int x, int y, int width, int height, int color) {
		fillRect(x, y, 1, height - 1, color);
		fillRect(x + width - 1, y + 1, 1, height - 1, color);
		fillRect(x + 1, y, width - 1, 1, color);
		fillRect(x, y + height - 1, width - 1, 1, color);
	}

	public static void drawCenteredString(TextRenderer renderer,
										  String text, int centerX, int y,
										  int color, boolean shadow) {
		drawString(renderer, text, centerX - renderer.getWidth(text) / 2,
			y,
			color, shadow);
	}

	public static void drawString(TextRenderer renderer, String text, int x, int y,
								  int color, boolean shadow) {
		if (shadow) {
			renderer.drawWithShadow(text, x, y, color);
		} else {
			renderer.draw(text, x, y, color);
		}
	}

	public static void bindTexture(Identifier texture) {
		Minecraft.getInstance().getTextureManager().bind(texture);
	}

	public static int nvgCreateImage(long ctx, Identifier texture) {
		return nvgCreateImage(ctx, texture, 0);
	}

	public static int nvgCreateImage(long ctx, Identifier texture, int imageFlags) {
		try {
			ByteBuffer buffer = mallocAndRead(Minecraft.getInstance().getResourceManager().getResource(texture)
				.asStream());
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
		setScissor(scissorStack.push(rect));
	}

	public static void popScissor() {
		scissorStack.pop();
		setScissor(scissorStack.empty() ? null : scissorStack.peek());
	}

	private static void setScissor(Rectangle rect) {
		if (rect != null) {
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			Window window = new Window(Minecraft.getInstance());
			int scale = window.getScale();
			GL11.glScissor(rect.x() * scale, (int) ((window.getScaledHeight() - rect.height() - rect.y()) * scale),
				rect.width() * scale, rect.height() * scale);
		} else {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}

	public static void drawScrollingText(String text, int x, int y, int width, int height, Color color) {
		drawScrollingText(x, y, x + width, y + height, text, color);
	}

	public static void drawScrollingText(int left, int top, int right, int bottom, String text, Color color) {
		drawScrollingText(Minecraft.getInstance().textRenderer, text, (left + right) / 2, left, top, right, bottom, color);
	}

	public static void drawScrollingText(TextRenderer renderer, String text, int center, int left, int top, int right, int bottom, Color color) {
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
			drawString(renderer, text, left - (int) g, y, color.toInt(), true);
			popScissor();
		} else {
			int min = left + textWidth / 2;
			int max = right - textWidth / 2;
			int centerX = center < min ? min : Math.min(center, max);
			drawCenteredString(renderer, text, centerX, y, color.toInt(), true);
		}
	}

	public static void drawTooltip(Option<?> option, int x, int y) {
		String tooltip = I18n.translate(option.getTooltip());
		if (tooltip.equals(option.getTooltip())) {
			return;
		}
		String[] text = tooltip.split("<br>");
		if (!text[0].isEmpty() || text.length > 1) {
			INSTANCE.renderTooltip(Arrays.asList(text), x-2, y+12+3+10);
		}

	}

	public void renderTooltip(List<String> list, int x, int y) {
		if (!list.isEmpty()) {
			GlStateManager.disableRescaleNormal();
			Lighting.turnOff();
			GlStateManager.disableLighting();
			GlStateManager.disableDepthTest();
			int k = 0;

			for (String string : list) {
				int l = Minecraft.getInstance().textRenderer.getWidth(string);
				if (l > k) {
					k = l;
				}
			}

			int m = x + 12;
			int n = y - 12;
			int o = 8;
			if (list.size() > 1) {
				o += 2 + (list.size() - 1) * 10;
			}

			if (m + k > Minecraft.getInstance().screen.width) {
				m -= 28 + k;
			}

			if (n + o + 6 > Minecraft.getInstance().screen.height) {
				n = Minecraft.getInstance().screen.height - o - 6;
			}

			drawOffset = 300.0F;
			Minecraft.getInstance().getItemRenderer().zOffset = 300.0F;
			int p = -267386864;
			fillGradient(m - 3, n - 4, m + k + 3, n - 3, p, p);
			fillGradient(m - 3, n + o + 3, m + k + 3, n + o + 4, p, p);
			fillGradient(m - 3, n - 3, m + k + 3, n + o + 3, p, p);
			fillGradient(m - 4, n - 3, m - 3, n + o + 3, p, p);
			fillGradient(m + k + 3, n - 3, m + k + 4, n + o + 3, p, p);
			int q = 1347420415;
			int r = (q & 16711422) >> 1 | q & 0xFF000000;
			fillGradient(m - 3, n - 3 + 1, m - 3 + 1, n + o + 3 - 1, q, r);
			fillGradient(m + k + 2, n - 3 + 1, m + k + 3, n + o + 3 - 1, q, r);
			fillGradient(m - 3, n - 3, m + k + 3, n - 3 + 1, q, q);
			fillGradient(m - 3, n + o + 2, m + k + 3, n + o + 3, r, r);

			for (int s = 0; s < list.size(); ++s) {
				String string2 = list.get(s);
				Minecraft.getInstance().textRenderer.drawWithShadow(string2, (float) m, (float) n, -1);
				if (s == 0) {
					n += 2;
				}

				n += 10;
			}

			drawOffset = 0.0F;
			Minecraft.getInstance().getItemRenderer().zOffset = 0.0F;
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			Lighting.turnOn();
			GlStateManager.enableRescaleNormal();
			GlStateManager.color3f(1, 1, 1);
		}
	}

	public static void drawTooltip(long ctx, NVGFont font, Option<?> option, int x, int y) {
		String tooltip = I18n.translate(option.getTooltip());
		if (tooltip.equals(option.getTooltip())) {
			return;
		}
		String[] text = tooltip.split("<br>");
		if (!text[0].isEmpty() || text.length > 1) {
			Screen screen = Minecraft.getInstance().screen;
			INSTANCE.drawTooltip(ctx, font, text, x, y, screen.width, screen.height);
		}

	}
}
