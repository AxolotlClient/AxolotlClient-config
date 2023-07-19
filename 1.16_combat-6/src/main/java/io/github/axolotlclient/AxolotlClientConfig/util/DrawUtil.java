package io.github.axolotlclient.AxolotlClientConfig.util;

import java.util.Stack;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.util.DrawUtility;
import io.github.axolotlclient.AxolotlClientConfig.common.util.Rectangle;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class DrawUtil extends DrawableHelper implements DrawUtility {

	private static final DrawUtil instance = new DrawUtil();

	public static DrawUtil getInstance() {
		return instance;
	}

	private final Stack<Rectangle> scissorStack = new Stack<>();

	private final MatrixStack stack = new MatrixStack();

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

	@Override
	public void pushScissor(Rectangle rect) {
		scissorStack.push(rect);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(ConfigUtils.toGlCoordsX(rect.x), ConfigUtils.toGlCoordsY(rect.y), rect.width, rect.height);
	}

	@Override
	public void popScissor() {
		scissorStack.pop();
		if (!scissorStack.empty()) {
			Rectangle rect = scissorStack.peek();
			GL11.glScissor(rect.x, rect.y, rect.width, rect.height);
		} else {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}

	@Override
	public void pushMatrices() {
		stack.push();
	}

	@Override
	public void popMatrices() {
		stack.pop();
	}

	@Override
	public void drawRect(int x, int y, int width, int height, int color) {
		fillRect(stack, x, y, width, height, color);
	}

	@Override
	public void drawCircle(int centerX, int centerY, int color, int radius, int startDeg, int endDeg) {
		pushMatrices();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();

		bb.begin(GL11.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = ((color) & 0xFF) / 255f;
		float a = ((color >> 24) & 0xFF) / 255f;

		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.disableTexture();

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
		GlStateManager.enableCull();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
		popMatrices();
	}

	@Override
	public void outlineCircle(int centerX, int centerY, int color, int radius, int startDeg, int endDeg) {
		pushMatrices();

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();

		bb.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);

		GL11.glLineWidth(2);

		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = ((color) & 0xFF) / 255f;
		float a = ((color >> 24) & 0xFF) / 255f;

		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.disableTexture();

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
			bb.vertex(x, y, 0).color(r, g, b, a).next();
		}

		tess.draw();
		GlStateManager.enableCull();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
		GL11.glLineWidth(1);
		popMatrices();
	}
}
