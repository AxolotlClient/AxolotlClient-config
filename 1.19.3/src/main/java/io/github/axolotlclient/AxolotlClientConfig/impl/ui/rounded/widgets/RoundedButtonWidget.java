package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGFont;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class RoundedButtonWidget extends ButtonWidget implements DrawingUtil {

	protected final static Color DEFAULT_BACKGROUND_COLOR = Colors.TURQUOISE;
	protected final Color activeColor = new Color(16777215);
	protected final Color inactiveColor = new Color(10526880);
	protected Color backgroundColor = DEFAULT_BACKGROUND_COLOR;

	public RoundedButtonWidget(int x, int y, Text message, PressAction action) {
		this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, action);
	}

	protected RoundedButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
	}

	protected static void drawScrollingText(DrawingUtil graphics, NVGFont font, Text text, int left, int top, int right, int bottom, Color color) {
		drawScrollingText(graphics, font, text, (left + right) / 2, left, top, right, bottom, color);
	}

	protected static void drawScrollingText(DrawingUtil drawingUtil, NVGFont font, Text text, int i, int j, int k, int l, int m, Color color) {
		float textWidth = font.getWidth(text.getString());
		int y = (k + m - 9) / 2 + 1;
		int width = l - j;
		if (textWidth > width) {
			float r = textWidth - width;
			double d = (double) Util.getMeasuringTimeMs() / 1000.0;
			double e = Math.max((double) r * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = MathHelper.lerp(f, 0.0, r);
			drawingUtil.pushScissor(NVGHolder.getContext(), j, k, l, m);
			drawingUtil.drawString(NVGHolder.getContext(), font, text.getString(), j - (int) g, y, color);
			drawingUtil.popScissor(NVGHolder.getContext());
		} else {
			float centerX = MathHelper.clamp(i, j + textWidth / 2, l - textWidth / 2);
			drawingUtil.drawCenteredString(NVGHolder.getContext(), font, text.getString(), centerX, y, color);
		}
	}

	@Override
	public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {

		fillRoundedRect(NVGHolder.getContext(), getX(), getY(), getWidth(), getHeight(), getWidgetColor(), Math.min(getHeight(), getHeight()) / 2f);

		if (isFocused()) {
			outlineRoundedRect(NVGHolder.getContext(), getX(), getY(), getWidth(), getHeight(), Colors.WHITE, Math.min(getHeight(), getHeight()) / 2f, 1);
		}

		Color i = this.active ? activeColor : inactiveColor;
		this.drawScrollableText(NVGHolder.getFont(), i.withAlpha((int) (this.alpha * 255)));
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

	public boolean isHovered() {
		return hovered;
	}
}
