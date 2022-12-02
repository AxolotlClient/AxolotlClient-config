package io.github.axolotlclient.AxolotlclientConfig.util;

import io.github.axolotlclient.AxolotlclientConfig.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * <p>
 * License: GPL-3.0
 */

public class DrawUtil extends DrawableHelper{

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
        if(shadow) {
            renderer.drawWithShadow(stack, text, x, y, color);
        }
        else {
            renderer.draw(stack, text, x, y, color);
        }
    }

}
