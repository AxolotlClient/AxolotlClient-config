package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Stack;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Rectangle;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class DrawUtil extends DrawableHelper implements DrawingUtil {

	private static Stack<Rectangle> scissorStack = new Stack<>();

    public static void fillRect(Rectangle rectangle, Color color) {
        fillRect(rectangle.x(), rectangle.y(), rectangle.width(),
                rectangle.height(),
                color.get().toInt());
    }

    public static void fillRect(int x, int y, int width, int height, int color) {
        DrawableHelper.fill(x, y, x + width, y + height, color);
    }

    public static void outlineRect(Rectangle rectangle, Color color) {
        outlineRect(rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color.get().toInt());
    }

    public static void outlineRect(int x, int y, int width, int height, int color) {
        fillRect(x, y, 1, height-1, color);
        fillRect(x + width - 1, y + 1, 1, height-1, color);
        fillRect(x+1, y, width-1, 1, color);
        fillRect(x, y + height - 1, width-1, 1, color);
    }

    public static void drawCenteredString(TextRenderer renderer,
                                          String text, int centerX, int y,
                                          int color, boolean shadow) {
        drawString(renderer, text, centerX - renderer.getStringWidth(text) / 2,
                y,
                color, shadow);
    }

    public static void drawString(TextRenderer renderer, String text, int x, int y,
                                  int color, boolean shadow) {
        if(shadow) {
            renderer.drawWithShadow(text, x, y, color);
        }
        else {
            renderer.draw(text, x, y, color);
        }
    }

	public static void bindTexture(Identifier texture){
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
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

			buffer.flip();

			return buffer;
		}
	}

	public static void pushScissor(int x, int y, int width, int height){
		pushScissor(new Rectangle(x, y, width, height));
	}

	public static void pushScissor(Rectangle rect){
		setScissor(scissorStack.push(rect));
	}

	public static void popScissor(){
		scissorStack.pop();
		setScissor(scissorStack.empty() ? null : scissorStack.peek());
	}

	private static void setScissor(Rectangle rect){
		if (rect != null) {
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			Window window = new Window(MinecraftClient.getInstance());
			int scale = window.getScaleFactor();
			GL11.glScissor(rect.x() * scale, (int) ((window.getScaledHeight() - rect.height() - rect.y()) * scale),
				rect.width() * scale, rect.height() * scale);
		} else {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}

}
