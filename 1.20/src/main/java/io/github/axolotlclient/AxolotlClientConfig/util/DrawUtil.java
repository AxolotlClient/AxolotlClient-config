package io.github.axolotlclient.AxolotlClientConfig.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.util.DrawUtility;
import io.github.axolotlclient.AxolotlClientConfig.common.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class DrawUtil implements DrawUtility {

	private static final DrawUtil instance = new DrawUtil();

	public static DrawUtil getInstance() {
		return instance;
	}

	public static void fillRect(GuiGraphics stack, Rectangle rectangle, Color color) {
		fillRect(stack, rectangle.x, rectangle.y, rectangle.width,
			rectangle.height,
			color.getAsInt());
	}

	public static void fillRect(GuiGraphics graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + height, color);
	}

	public static void outlineRect(GuiGraphics stack, Rectangle rectangle, Color color) {
		outlineRect(stack, rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.getAsInt());
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

	public void pushScissor(Rectangle rect) {
		getGraphics().enableScissor(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
	}

	private GuiGraphics graphics;

	private GuiGraphics getGraphics() {
		if (graphics == null) {
			graphics = new GuiGraphics(MinecraftClient.getInstance(), MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers());
		}
		return graphics;
	}

	public void popScissor() {
		getGraphics().disableScissor();
	}

	public void pushMatrices() {
		getGraphics().getMatrices().push();
	}

	public void popMatrices() {
		getGraphics().getMatrices().pop();
	}

	@Override
	public void drawRect(int x, int y, int width, int height, int color) {
		fillRect(getGraphics(), x, y, width, height, color);
	}

	@Override
	public void drawCircle(int centerX, int centerY, int color, int radius, int startDeg, int endDeg) {
		pushMatrices();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBufferBuilder();

		bb.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
		Matrix4f matrix = getGraphics().getMatrices().peek().getModel();


		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = ((color) & 0xFF) / 255f;
		float a = ((color >> 24) & 0xFF) / 255f;

		RenderSystem.enableBlend();
		RenderSystem.disableCull();

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
		RenderSystem.disableBlend();
		popMatrices();
	}

	@Override
	public void outlineCircle(int centerX, int centerY, int color, int radius, int startDeg, int endDeg) {
		pushMatrices();

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBufferBuilder();

		bb.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

		Matrix4f matrix4f = getGraphics().getMatrices().peek().getModel();

		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = ((color) & 0xFF) / 255f;
		float a = ((color >> 24) & 0xFF) / 255f;

		RenderSystem.enableBlend();
		RenderSystem.disableCull();

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
		RenderSystem.disableBlend();
		popMatrices();
	}

	@Override
	public void fill(float x1, float y1, float x2, float y2, int color) {

		Matrix4f matrix = getGraphics().getMatrices().peek().getModel();

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
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x1, y2, 0.0F).color(g, h, j, f).next();
		bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(g, h, j, f).next();
		bufferBuilder.vertex(matrix, x2, y1, 0.0F).color(g, h, j, f).next();
		bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(g, h, j, f).next();
		BufferRenderer.drawWithShader(bufferBuilder.end());
		RenderSystem.disableBlend();
	}

	@Override
	public void drawRect(int x, int y, int width, int height, int color, int cornerRadiusIfRounded) {
		if (AxolotlClientConfigConfig.roundedRects.get()) {
			drawRoundedRect(x, y, width, height, color, cornerRadiusIfRounded);
		} else {
			drawRect(x, y, width, height, color);
		}
	}

	@Override
	public void outlineRect(int x, int y, int width, int height, int color, int cornerRadiusIfRounded) {
		if (AxolotlClientConfigConfig.roundedRects.get()) {
			outlineRoundedRect(x, y, width, height, color, cornerRadiusIfRounded);
		} else {
			outlineRect(getGraphics(), x, y, width, height, color);
		}
	}

	public void drawRect(Rectangle rect, Color color, int cornerRadiusIfRounded) {
		drawRect(rect, color.getAsInt(), cornerRadiusIfRounded);
	}

	public void outlineRect(Rectangle rectangle, Color color, int cornerRadiusIfRounded) {
		outlineRect(rectangle, color.getAsInt(), cornerRadiusIfRounded);
	}

	public void drawRect(int x, int y, int width, int height, Color color, int cornerRadiusIfRounded) {
		drawRect(x, y, width, height, color.getAsInt(), cornerRadiusIfRounded);
	}

	public void outlineRect(int x, int y, int width, int height, Color color, int cornerRadiusIfRounded) {
		outlineRect(x, y, width, height, color.getAsInt(), cornerRadiusIfRounded);
	}
}
