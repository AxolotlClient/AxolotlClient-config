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
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.*;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.util.RoundedDrawUtil;

public class RoundedButtonWidget extends ButtonWidget implements DrawingUtil, Drawable, Selectable {

	protected final static Color DEFAULT_BACKGROUND_COLOR = Colors.accent();
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
		RoundedDrawUtil.drawScrollingText(graphics, font, text, (left + right) / 2, left, top, right, bottom, color);
	}

	@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {

		fillRoundedRect(NVGHolder.getContext(), getX(), getY(), getWidth(), getHeight(), getWidgetColor(), Math.min(getHeight(), getHeight()) / 2f);

		if (isFocused()) {
			outlineRoundedRect(NVGHolder.getContext(), getX(), getY(), getWidth(), getHeight(), Colors.highlight(), Math.min(getHeight(), getHeight()) / 2f, 1);
		}

		Color i = this.active ? activeColor : inactiveColor;
		this.drawScrollableText(NVGHolder.getFont(), i.withAlpha(255));
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
		return hovered && this.active ? Colors.accent2() : backgroundColor;
	}

	public boolean isHovered() {
		return hovered;
	}

	@Override
	public SelectionType getType() {
		return isHovered() ? SelectionType.HOVERED : (isFocused() ? SelectionType.FOCUSED : SelectionType.NONE);
	}
}
