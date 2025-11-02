/*
 * Copyright © 2021-2024 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;


import net.minecraft.client.render.TextRenderer;
import net.minecraft.text.Formatting;

public class PlainTextButtonWidget extends VanillaButtonWidget {
	private final TextRenderer font;
	private final String content;
	private final String underlinedContent;

	public PlainTextButtonWidget(int x, int y, int width, int height, String content, PressAction empty, TextRenderer font) {
		super(x, y, width, height, content, empty);
		this.font = font;
		this.content = content;
		this.underlinedContent = Formatting.UNDERLINE + content;
	}

	@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {
		String text = this.isHovered() ? this.underlinedContent : this.content;
		font.drawWithShadow(text, getX(), getY(), 16777215 | 255 << 24);
	}
}
