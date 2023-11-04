package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public class DrawUtil {

	public static void fillRect(MatrixStack stack, Rectangle rectangle, Color color) {
		fillRect(stack, rectangle.x(), rectangle.y(), rectangle.width(),
			rectangle.height(),
			color.toInt());
	}

	public static void fillRect(MatrixStack graphics, int x, int y, int width, int height, int color) {
		DrawableHelper.fill(graphics, x, y, x + width, y + height, color);
	}

	public static void outlineRect(MatrixStack stack, Rectangle rectangle, Color color) {
		outlineRect(stack, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color.toInt());
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

}
