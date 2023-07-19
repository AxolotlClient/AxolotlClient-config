package io.github.axolotlclient.AxolotlClientConfig.util;

import java.util.Stack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.util.DrawUtility;
import io.github.axolotlclient.AxolotlClientConfig.common.util.Rectangle;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * <p>
 * License: GPL-3.0
 */

public class DrawUtil extends DrawableHelper implements DrawUtility {

	private static final DrawUtil instance = new DrawUtil();

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
		enableScissor(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
	}

	@Override
	public void popScissor() {
		scissorStack.pop();
		if (!scissorStack.empty()) {
			Rectangle rect = scissorStack.peek();
			enableScissor(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
		} else {
			disableScissor();
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
		RenderSystem.enableBlend();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBufferBuilder();
		builder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

		builder.vertex(centerX, centerY, 0).color(color);

		for (float pos = startDeg; pos <= endDeg; pos++) {
			builder.vertex(radius * Math.cos(Math.PI * pos / 180) + centerX,
				radius * Math.sin(Math.PI * pos / 180) + centerY, 0).color(color);
		}
		BufferRenderer.draw(builder.end());
	}
}
