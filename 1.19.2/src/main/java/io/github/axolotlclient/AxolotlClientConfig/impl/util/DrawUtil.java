package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Stack;

import com.mojang.blaze3d.glfw.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Rectangle;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class DrawUtil implements DrawingUtil {

	private static final Stack<Rectangle> scissorStack = new Stack<>();

    public static void fillRect(MatrixStack matrices, Rectangle rectangle, Color color) {
        fillRect(matrices, rectangle.x(), rectangle.y(), rectangle.width(),
                rectangle.height(),
                color.get().toInt());
    }

	public static void fill(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int color){
		DrawableHelper.fill(matrixStack, x1, y1, x2, y2, color);
	}

    public static void fillRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        DrawableHelper.fill(matrices, x, y, x + width, y + height, color);
    }

    public static void outlineRect(MatrixStack matrices, Rectangle rectangle, Color color) {
        outlineRect(matrices, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color.get().toInt());
    }

    public static void outlineRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        fillRect(matrices, x, y, 1, height-1, color);
        fillRect(matrices, x + width - 1, y + 1, 1, height-1, color);
        fillRect(matrices, x+1, y, width-1, 1, color);
        fillRect(matrices, x, y + height - 1, width-1, 1, color);
    }

    public static void drawCenteredString(MatrixStack matrices, TextRenderer renderer,
                                          String text, int centerX, int y,
                                          int color, boolean shadow) {
        drawString(matrices, renderer, text, centerX - renderer.getWidth(text) / 2,
                y,
                color, shadow);
    }

    public static void drawString(MatrixStack matrices, TextRenderer renderer, String text, int x, int y,
                                  int color, boolean shadow) {
        if(shadow) {
            renderer.drawWithShadow(matrices, text, x, y, color);
        }
        else {
            renderer.draw(matrices, text, x, y, color);
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
			Window window = MinecraftClient.getInstance().getWindow();
			int scale = (int) window.getScaleFactor();
			GL11.glScissor(rect.x() * scale, (int) ((window.getScaledHeight() - rect.height() - rect.y()) * scale),
				rect.width() * scale, rect.height() * scale);
		} else {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}

	public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
		drawTexture(matrices, x, y, 0, (float)u, (float)v, width, height, 256, 256);
	}

	public static void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		drawTexture(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
	}

	public static void drawTexture(
		MatrixStack matrices, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight
	) {
		drawTexture(matrices, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
	}

	public static void drawTexture(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		drawTexture(matrices, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
	}

	private static void drawTexture(
		MatrixStack matrices, int x0, int x1, int y0, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight
	) {
		drawTexturedQuad(
			matrices.peek().getModel(),
			x0,
			x1,
			y0,
			y1,
			z,
			(u + 0.0F) / (float)textureWidth,
			(u + (float)regionWidth) / (float)textureWidth,
			(v + 0.0F) / (float)textureHeight,
			(v + (float)regionHeight) / (float)textureHeight
		);
	}

	private static void drawTexturedQuad(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix, (float)x0, (float)y1, (float)z).uv(u0, v1).next();
		bufferBuilder.vertex(matrix, (float)x1, (float)y1, (float)z).uv(u1, v1).next();
		bufferBuilder.vertex(matrix, (float)x1, (float)y0, (float)z).uv(u1, v0).next();
		bufferBuilder.vertex(matrix, (float)x0, (float)y0, (float)z).uv(u0, v0).next();
		BufferRenderer.drawWithShader(bufferBuilder.end());
	}

}
