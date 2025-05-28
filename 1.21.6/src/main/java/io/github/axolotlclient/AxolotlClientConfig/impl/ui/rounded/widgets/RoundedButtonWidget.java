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
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class RoundedButtonWidget extends Button implements DrawingUtil {

	protected final static Color DEFAULT_BACKGROUND_COLOR = Colors.accent();
	protected final Color activeColor = Colors.text();
	protected final Color inactiveColor = new Color(0xA0A0A0);
	protected Color backgroundColor = DEFAULT_BACKGROUND_COLOR;

	public RoundedButtonWidget(int x, int y, Component message, OnPress action) {
		this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, action);
	}

	protected RoundedButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
	}

	protected static void drawScrollingText(DrawingUtil graphics, NVGFont font, Component text, int left, int top, int right, int bottom, Color color) {
		DrawUtil.drawScrollingText(graphics, font, text, (left + right) / 2, left, top, right, bottom, color);
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

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
