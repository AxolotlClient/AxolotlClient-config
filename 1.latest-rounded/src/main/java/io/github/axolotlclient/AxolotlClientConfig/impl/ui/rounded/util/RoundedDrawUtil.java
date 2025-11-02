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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Rectangle;
import io.github.axolotlclient.AxolotlClientConfig.impl.mixin.NativeImageInvoker;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGFont;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public class RoundedDrawUtil implements DrawingUtil {

	private static final RoundedDrawUtil INSTANCE = new RoundedDrawUtil();

	public static int nvgCreateImage(long ctx, ResourceLocation texture) {
		return nvgCreateImage(ctx, texture, 0);
	}

	public static int nvgCreateImage(long ctx, ResourceLocation texture, int imageFlags) {
		try {
			ByteBuffer buffer = mallocAndRead(Minecraft.getInstance().getResourceManager().getResource(texture)
				.orElseThrow().open());
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

			buffer.flip();

			return buffer;
		}
	}

	public static void drawScrollingText(DrawingUtil drawingUtil, NVGFont font, Component text, int center, int left, int top, int right, int bottom, Color color) {
		float textWidth = font.getWidth(text.getString());
		int y = (top + bottom - 9) / 2 + 1;
		int width = right - left;
		if (textWidth > width) {
			float r = textWidth - width;
			double d = (double) Util.getMillis() / 1000.0;
			double e = Math.max((double) r * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = Mth.lerp(f, 0.0, r);
			drawingUtil.pushScissor(NVGHolder.getContext(), left, top, right, bottom);
			drawingUtil.drawString(NVGHolder.getContext(), font, getFormattedString(text), left - (int) g, y, color);
			drawingUtil.popScissor(NVGHolder.getContext());
		} else {
			float centerX = Mth.clamp(center, left + textWidth / 2, right - textWidth / 2);
			drawingUtil.drawCenteredString(NVGHolder.getContext(), font, getFormattedString(text), centerX, y, color);
		}
	}

	public static void drawTooltip(long ctx, NVGFont font, Option<?> option, int x, int y) {
		String tooltip = I18n.get(option.getTooltip());
		if (tooltip.equals(option.getTooltip())) {
			return;
		}
		String[] text = tooltip.split("<br>");
		if (!text[0].isEmpty() || text.length > 1) {
			Screen screen = Minecraft.getInstance().screen;
			// Uhhh hm?
			Font renderer = Minecraft.getInstance().font;
			text = Arrays.stream(text).map(Component::nullToEmpty)
				.flatMap((text1) -> renderer.splitIgnoringLanguage(text1, 170).stream())
				.map(FormattedText::getString)
				.toArray(String[]::new);

			INSTANCE.drawTooltip(ctx, font, text, x, y, screen.width, screen.height);
		}
	}

	public static String getFormattedString(FormattedText component) {
		StringBuilder builder = new StringBuilder();
		component.visit((style, string) -> {
			if (style.getColor() != null && !style.getColor().serialize().contains("#")) {
				builder.append(ChatFormatting.getByName(style.getColor().serialize()));
			}
			if (style.isBold()) {
				builder.append(ChatFormatting.BOLD);
			}
			if (style.isUnderlined()) {
				builder.append(ChatFormatting.UNDERLINE);
			}
			if (style.isObfuscated()) {
				builder.append(ChatFormatting.OBFUSCATED);
			}
			if (style.isItalic()) {
				builder.append(ChatFormatting.ITALIC);
			}
			if (style.isStrikethrough()) {
				builder.append(ChatFormatting.STRIKETHROUGH);
			}
			builder.append(string);
			builder.append(ChatFormatting.RESET);
			return Optional.empty();
		}, Style.EMPTY);
		return builder.toString();
	}
}
