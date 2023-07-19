package io.github.axolotlclient.AxolotlClientConfig.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.util.DrawUtility;
import io.github.axolotlclient.AxolotlClientConfig.common.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * <p>
 * License: GPL-3.0
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

	public void drawRect(int x, int y, int width, int height, int color) {
		pushMatrices();
		fillRect(graphics, x, y, width, height, color);
		popMatrices();
	}

	@Override
	public void drawCircle(int centerX, int centerY, int color, int radius, int startDeg, int endDeg) {
		pushMatrices();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBufferBuilder();

		bb.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

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

		bb.vertex(centerX, centerY, 0).color(r, g, b, a).next();
		for (double i = startDeg; i <= endDeg; i++) {
			double t = i / 360;
			final float TAU = (float) (Math.PI * 2);
			float x = (float) (centerX + (Math.sin(t * TAU) * radius));
			float y = (float) (centerY + (Math.cos(t * TAU) * radius));
			bb.vertex(x, y, 0).color(r, g, b, a).next();
		}

		tess.draw();
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
		popMatrices();
	}

	@Override
	public void outlineCircle(int centerX, int centerY, int color, int radius, int startDeg, int endDeg) {
		pushMatrices();

		Matrix4f matrix4f = getGraphics().getMatrices().peek().getModel();
		GL11.glLineWidth(2);
		GL11.glBegin(GL11.GL_LINE_STRIP);

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
			GL11.glColor4f(r, g, b, a);
			GL11.glVertex2d(x, y);
		}

		GL11.glEnd();
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
		popMatrices();
	}

	public void drawRoundedRect(Rectangle rect, int color, int cornerRadius) {
		drawRoundedRect(rect.x, rect.y, rect.width, rect.height, color, cornerRadius);
	}

	public void drawRoundedRect(int x, int y, int width, int height, int color, int cornerRadius) {

		drawCircle(x + cornerRadius, y + cornerRadius, color, cornerRadius, 90, 180);
		drawCircle(x + width - cornerRadius, y + cornerRadius, color, cornerRadius, 0, 90);
		drawCircle(x + width - cornerRadius, y + height - cornerRadius, color, cornerRadius, 270, 360);
		drawCircle(x + cornerRadius, y + height - cornerRadius, color, cornerRadius, 180, 270);

		drawRect(x + cornerRadius, y, width - (cornerRadius * 2), cornerRadius, color);
		drawRect(x, y + cornerRadius, width, height - (cornerRadius * 2), color);
		drawRect(x + cornerRadius, y + height - cornerRadius, width - (cornerRadius * 2), cornerRadius, color);

	}

	public void outlineRoundedRect(Rectangle rect, int color, int cornerRadius) {
		outlineRoundedRect(rect.x, rect.y, rect.width, rect.height, color, cornerRadius);
	}

	public void outlineRoundedRect(int x, int y, int width, int height, int color, int cornerRadius) {
		outlineCircle(x + cornerRadius, y + cornerRadius, color, cornerRadius, 90, 180);
		outlineCircle(x + width - cornerRadius, y + cornerRadius, color, cornerRadius, 0, 90);
		outlineCircle(x + width - cornerRadius, y + height - cornerRadius, color, cornerRadius, 270, 360);
		outlineCircle(x + cornerRadius, y + height - cornerRadius, color, cornerRadius, 180, 270);

		drawRect(x + cornerRadius, y - 1, width - (cornerRadius * 2), 1, color);
		drawRect(x - 1, y + cornerRadius, 1, height - (cornerRadius * 2), color);
		drawRect(x + cornerRadius, y + height, width - (cornerRadius * 2), 1, color);
		drawRect(x + width, y + cornerRadius, 1, height - cornerRadius * 2, color);
	}

	public void outlineCircle(int centerX, int centerY, int color, int radius) {
		outlineCircle(centerX, centerY, color, radius, 0, 360);
	}
}
