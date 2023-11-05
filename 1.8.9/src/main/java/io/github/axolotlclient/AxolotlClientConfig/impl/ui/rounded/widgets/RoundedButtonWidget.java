package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.*;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class RoundedButtonWidget extends ButtonWidget implements DrawingUtil, Drawable, Selectable {

	protected final static Color DEFAULT_BACKGROUND_COLOR = Colors.TURQUOISE;
	protected final Color activeColor = new Color(16777215);
	protected final Color inactiveColor = new Color(10526880);
	protected Color backgroundColor = DEFAULT_BACKGROUND_COLOR;

	public RoundedButtonWidget(int x, int y, String message, PressAction action) {
		this(x, y, 150, 20, message, action);
	}

	protected RoundedButtonWidget(int x, int y, int width, int height, String message, PressAction onPress) {
		super(x, y, width, height, message, onPress);
	}

	protected static void drawScrollingText(DrawingUtil graphics, NVGFont font, String text, int left, int top, int right, int bottom, Color color) {
		drawScrollingText(graphics, font, text, (left + right) / 2, left, top, right, bottom, color);
	}

	protected static void drawScrollingText(DrawingUtil drawingUtil, NVGFont font, String text, int i, int j, int k, int l, int m, Color color) {
		float textWidth = font.getWidth(text);
		int y = (k + m - 9) / 2 + 1;
		int width = l - j;
		if (textWidth > width) {
			float r = textWidth - width;
			double d = (double) MinecraftClient.getTime() / 1000.0;
			double e = Math.max((double) r * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = MathHelper.clampedLerp(f, 0.0, r);
			drawingUtil.pushScissor(NVGHolder.getContext(), j, k, l, m);
			drawingUtil.drawString(NVGHolder.getContext(), font, text, j - (int) g, y, color);
			drawingUtil.popScissor(NVGHolder.getContext());
		} else {
			float centerX = MathHelper.clamp(i, j + textWidth / 2, l - textWidth / 2);
			drawingUtil.drawCenteredString(NVGHolder.getContext(), font, text, centerX, y, color);
		}
	}

	@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {

		fillRoundedRect(NVGHolder.getContext(), getX(), getY(), getWidth(), getHeight(), getWidgetColor(), Math.min(getHeight(), getHeight()) / 2f);

		if (isFocused()) {
			outlineRoundedRect(NVGHolder.getContext(), getX(), getY(), getWidth(), getHeight(), Colors.WHITE, Math.min(getHeight(), getHeight()) / 2f, 1);
		}

		Color i = this.active ? activeColor : inactiveColor;
		this.drawScrollableText(NVGHolder.getFont(), i.withAlpha((int) (1 * 255)));
	}

	private void drawScrollableText(NVGFont font, Color color) {
		drawScrollingText(font, 2, color);
	}

	protected void drawScrollingText(NVGFont font, int xOffset, Color color) {
		int i = this.getX() + xOffset;
		int j = this.getX() + this.getWidth() - xOffset;
		drawScrollingText(this, font, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), color);
	}

	protected Color getWidgetColor() {
		return hovered && this.active ? Colors.DARK_YELLOW : backgroundColor;
	}

	public boolean isHovered(){
		return hovered;
	}

	@Override
	public SelectionType getType() {
		return isHovered() ? SelectionType.HOVERED : (isFocused() ? SelectionType.FOCUSED : SelectionType.NONE);
	}
}
