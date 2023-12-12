/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGFont;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class RoundedButtonWidget extends ButtonWidget implements DrawingUtil {

	protected final static Color DEFAULT_BACKGROUND_COLOR = Colors.accent();
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

	protected static void drawScrollingText(DrawingUtil drawingUtil, NVGFont font, Text text, int center, int left, int top, int right, int bottom, Color color) {
		float textWidth = font.getWidth(text.getString());
		int y = (top + bottom - 9) / 2 + 1;
		int width = right - left;
		if (textWidth > width) {
			float r = textWidth - width;
			double d = (double) Util.getMeasuringTimeMs() / 1000.0;
			double e = Math.max((double) r * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = MathHelper.lerp(f, 0.0, r);
			drawingUtil.pushScissor(NVGHolder.getContext(), left, top, right, bottom);
			drawingUtil.drawString(NVGHolder.getContext(), font, text.getString(), left - (int) g, y, color);
			drawingUtil.popScissor(NVGHolder.getContext());
		} else {
			float centerX = MathHelper.clamp(center, left + textWidth / 2, right - textWidth / 2);
			drawingUtil.drawCenteredString(NVGHolder.getContext(), font, text.getString(), centerX, y, color);
		}
	}

	@Override
	protected void drawWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

		fillRoundedRect(NVGHolder.getContext(), getX(), getY(), getWidth(), getHeight(), getWidgetColor(), Math.min(getHeight(), getHeight()) / 2f);

		if (isFocused()) {
			outlineRoundedRect(NVGHolder.getContext(), getX(), getY(), getWidth(), getHeight(), Colors.highlight(), Math.min(getHeight(), getHeight()) / 2f, 1);
		}

		Color i = this.active ? activeColor : inactiveColor;
		this.drawScrollableText(NVGHolder.getFont(), i.withAlpha((int) (this.alpha * 255)));
	}

	private void drawScrollableText(NVGFont font, Color color) {
		drawScrollingText(font, TEXT_MARGIN, color);
	}

	protected void drawScrollingText(NVGFont font, int xOffset, Color color) {
		int i = this.getX() + xOffset;
		int j = this.getX() + this.getWidth() - xOffset;
		drawScrollingText(this, font, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), color);
	}

	protected Color getWidgetColor() {
		return isHovered() && this.active ? Colors.accent2() : backgroundColor;
	}
}
