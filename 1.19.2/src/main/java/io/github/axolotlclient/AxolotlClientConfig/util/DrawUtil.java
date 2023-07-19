package io.github.axolotlclient.AxolotlClientConfig.util;

import java.util.Stack;

import com.mojang.blaze3d.glfw.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class DrawUtil extends DrawableHelper {

	private static final DrawUtil instance = new DrawUtil();

	public static DrawUtil getInstance() {
		return instance;
	}

	private final Stack<Rectangle> scissorStack = new Stack<>();

	public static void fillRect(MatrixStack stack, Rectangle rectangle, Color color) {
		fillRect(stack, rectangle.x, rectangle.y, rectangle.width,
			rectangle.height,
			color.getAsInt());
	}

	public static void fillRect(MatrixStack stack, int x, int y, int width, int height, int color) {
		DrawableHelper.fill(stack, x, y, x + width, y + height, color);
	}

    public static void outlineRect(MatrixStack stack, Rectangle rectangle, Color color) {
        outlineRect(stack, rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.getAsInt());
    }

    public static void outlineRect(MatrixStack stack, int x, int y, int width, int height, int color) {
        fillRect(stack, x, y, 1, height-1, color);
        fillRect(stack, x + width - 1, y + 1, 1, height-1, color);
        fillRect(stack, x+1, y, width-1, 1, color);
        fillRect(stack, x, y + height - 1, width-1, 1, color);
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
		if (shadow) {
			renderer.drawWithShadow(stack, text, x, y, color);
		} else {
			renderer.draw(stack, text, x, y, color);
		}
	}

	public void pushScissor(Rectangle rect) {
		scissorStack.push(rect);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		Window window = MinecraftClient.getInstance().getWindow();
		double scale = window.getScaleFactor();
		GL11.glScissor((int) (rect.x * scale), (int) ((window.getScaledHeight() - rect.height - rect.y) * scale), (int) (rect.width * scale), (int) (rect.height * scale));
	}

	public void popScissor() {
		scissorStack.pop();
		if (!scissorStack.empty()) {
			Rectangle rect = scissorStack.peek();
			Window window = MinecraftClient.getInstance().getWindow();
			double scale = window.getScaleFactor();
			GL11.glScissor((int) (rect.x * scale), (int) ((window.getScaledHeight() - rect.height - rect.y) * scale), (int) (rect.width * scale), (int) (rect.height * scale));
		} else {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}

	public void drawRect(MatrixStack stack, int x, int y, int width, int height, int color) {
		fillRect(stack, x, y, width, height, color);
	}

	public void drawCircle(MatrixStack stack, int centerX, int centerY, int color, int radius, int startDeg, int endDeg) {
		stack.push();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBufferBuilder();

		bb.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
		Matrix4f matrix = stack.peek().getModel();


		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = ((color) & 0xFF) / 255f;
		float a = ((color >> 24) & 0xFF) / 255f;

		RenderSystem.enableBlend();
		RenderSystem.disableCull();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		if (startDeg < 0) {
			startDeg += 360;
		} else if (startDeg > 360) {
			startDeg -= 360;
		}

		if (endDeg < 0) {
			endDeg += 360;
		} else if (endDeg > 360) {
			endDeg -= 360;
		}

		if (startDeg > endDeg) {
			int d = startDeg;
			startDeg = endDeg;
			endDeg = d;
		}

		startDeg += 90;
		endDeg += 90;

		bb.vertex(matrix, centerX, centerY, 0).color(r, g, b, a).next();
		for (int i = startDeg; i <= endDeg; i++) {
			float t = (i / 360F);
			final float TAU = (float) (Math.PI * 2);
			bb.vertex(matrix, (float) (centerX + (Math.sin(t * TAU) * radius)), (float) (centerY + (Math.cos(t * TAU) * radius)), 0).color(r, g, b, a).next();
		}

		tess.draw();
		RenderSystem.enableCull();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		stack.pop();
	}

	public void outlineCircle(MatrixStack stack, int centerX, int centerY, int color, int radius, int startDeg, int endDeg) {
		stack.push();

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBufferBuilder();

		bb.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

		Matrix4f matrix4f = stack.peek().getModel();

		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = ((color) & 0xFF) / 255f;
		float a = ((color >> 24) & 0xFF) / 255f;

		RenderSystem.enableBlend();
		RenderSystem.disableCull();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		if (startDeg < 0) {
			startDeg += 360;
		} else if (startDeg > 360) {
			startDeg -= 360;
		}

		if (endDeg < 0) {
			endDeg += 360;
		} else if (endDeg > 360) {
			endDeg -= 360;
		}

		if (startDeg > endDeg) {
			int d = startDeg;
			startDeg = endDeg;
			endDeg = d;
		}

		startDeg += 90;
		endDeg += 90;

		for (double i = startDeg; i <= endDeg; i++) {
			double t = i / 360;
			final float TAU = (float) (Math.PI * 2);
			float x = (float) (centerX + (Math.sin(t * TAU) * radius));
			float y = (float) (centerY + (Math.cos(t * TAU) * radius));
			bb.vertex(matrix4f, x, y, 0).color(r, g, b, a).next();
		}

		tess.draw();
		RenderSystem.enableCull();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		stack.pop();
	}

	public void fill(MatrixStack stack, float x1, float y1, float x2, float y2, int color) {

		Matrix4f matrix = stack.peek().getModel();

		if (x1 < x2) {
			float i = x1;
			x1 = x2;
			x2 = i;
		}

		if (y1 < y2) {
			float i = y1;
			y1 = y2;
			y2 = i;
		}

		float f = (float)(color >> 24 & 0xFF) / 255.0F;
		float g = (float)(color >> 16 & 0xFF) / 255.0F;
		float h = (float)(color >> 8 & 0xFF) / 255.0F;
		float j = (float)(color & 0xFF) / 255.0F;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x1, y2, 0.0F).color(g, h, j, f).next();
		bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(g, h, j, f).next();
		bufferBuilder.vertex(matrix, x2, y1, 0.0F).color(g, h, j, f).next();
		bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(g, h, j, f).next();
		BufferRenderer.drawWithShader(bufferBuilder.end());
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public void drawRect(MatrixStack stack, int x, int y, int width, int height, int color, int cornerRadiusIfRounded) {
		if (AxolotlClientConfigConfig.roundedRects.get()) {
			drawRoundedRect(stack, x, y, width, height, color, cornerRadiusIfRounded);
		} else {
			drawRect(stack, x, y, width, height, color);
		}
	}

	public void outlineRect(MatrixStack stack, int x, int y, int width, int height, int color, int cornerRadiusIfRounded) {
		if (AxolotlClientConfigConfig.roundedRects.get()) {
			outlineRoundedRect(stack, x, y, width, height, color, cornerRadiusIfRounded);
		} else {
			outlineRect(stack, x, y, width, height, color);
		}
	}

	public void drawRect(MatrixStack stack, Rectangle rect, Color color, int cornerRadiusIfRounded) {
		drawRect(stack, rect, color.getAsInt(), cornerRadiusIfRounded);
	}

	public void outlineRect(MatrixStack stack, Rectangle rectangle, Color color, int cornerRadiusIfRounded) {
		outlineRect(stack, rectangle, color.getAsInt(), cornerRadiusIfRounded);
	}

	public void drawRect(MatrixStack stack, int x, int y, int width, int height, Color color, int cornerRadiusIfRounded) {
		drawRect(stack, x, y, width, height, color.getAsInt(), cornerRadiusIfRounded);
	}

	public void outlineRect(MatrixStack stack, int x, int y, int width, int height, Color color, int cornerRadiusIfRounded) {
		outlineRect(stack, x, y, width, height, color.getAsInt(), cornerRadiusIfRounded);
	}

	public void drawRect(MatrixStack stack, Rectangle rect, int color) {
		drawRect(stack, rect.x, rect.y, rect.width, rect.height, color);
	}

	public void drawCircle(MatrixStack stack, int centerX, int centerY, int color, int radius) {
		drawCircle(stack, centerX, centerY, color, radius, 0, 360);
	}

	public void drawRoundedRect(MatrixStack stack, Rectangle rect, int color, int cornerRadius) {
		drawRoundedRect(stack, rect.x, rect.y, rect.width, rect.height, color, cornerRadius);
	}

	public void drawRoundedRect(MatrixStack stack, int x, int y, int width, int height, int color, int cornerRadius) {

		cornerRadius = Math.min(cornerRadius, Math.min(height, width) / 2);

		drawCircle(stack, x + cornerRadius, y + cornerRadius, color, cornerRadius, 90, 180);
		drawCircle(stack, x + width - cornerRadius, y + cornerRadius, color, cornerRadius, 0, 90);
		drawCircle(stack, x + width - cornerRadius, y + height - cornerRadius, color, cornerRadius, 270, 360);
		drawCircle(stack, x + cornerRadius, y + height - cornerRadius, color, cornerRadius, 180, 270);

		drawRect(stack, x + cornerRadius, y, width - (cornerRadius * 2), cornerRadius, color);
		drawRect(stack, x, y + cornerRadius, width, height - (cornerRadius * 2), color);
		drawRect(stack, x + cornerRadius, y + height - cornerRadius, width - (cornerRadius * 2), cornerRadius, color);

	}

	public void outlineRoundedRect(MatrixStack stack, Rectangle rect, int color, int cornerRadius) {
		outlineRoundedRect(stack, rect.x, rect.y, rect.width, rect.height, color, cornerRadius);
	}

	public void outlineRoundedRect(MatrixStack stack, int x, int y, int width, int height, int color, int cornerRadius) {

		cornerRadius = Math.min(cornerRadius, Math.min(height, width) / 2);

		outlineCircle(stack, x + cornerRadius, y + cornerRadius, color, cornerRadius, 90, 180);
		outlineCircle(stack, x + width - cornerRadius, y + cornerRadius, color, cornerRadius, 0, 90);
		outlineCircle(stack, x + width - cornerRadius, y + height - cornerRadius, color, cornerRadius, 270, 360);
		outlineCircle(stack, x + cornerRadius, y + height - cornerRadius, color, cornerRadius, 180, 270);

		drawLine(stack, x + cornerRadius, y, x + width - (cornerRadius), y, color);
		drawLine(stack, x, y + cornerRadius, x, y + height - (cornerRadius), color);
		drawLine(stack, x + cornerRadius, y + height, x + width - (cornerRadius), y + height, color);
		drawLine(stack, x + width, y + cornerRadius, x + width, y + height - cornerRadius, color);
	}

	public void drawLine(MatrixStack stack, int x, int y, int x2, int y2, int color){
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBufferBuilder();

		bb.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

		Matrix4f matrix4f = stack.peek().getModel();

		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = ((color) & 0xFF) / 255f;
		float a = ((color >> 24) & 0xFF) / 255f;

		RenderSystem.enableBlend();
		RenderSystem.disableCull();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);


		bb.vertex(matrix4f, x, y, 0).color(r, g, b, a).next();
		bb.vertex(matrix4f, x2, y2, 0).color(r, g, b, a).next();

		tess.draw();
		RenderSystem.enableCull();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public void outlineCircle(MatrixStack stack, int centerX, int centerY, int color, int radius) {
		outlineCircle(stack, centerX, centerY, color, radius, 0, 360);
	}

	public void drawRect(MatrixStack stack, Rectangle rect, int color, int cornerRadiusIfRounded) {
		drawRect(stack, rect.x, rect.y, rect.width, rect.height, color, cornerRadiusIfRounded);
	}

	public void outlineRect(MatrixStack stack, Rectangle rect, int color, int cornerRadiusIfRounded) {
		outlineRect(stack, rect.x, rect.y, rect.width, rect.height, color, cornerRadiusIfRounded);
	}
}
