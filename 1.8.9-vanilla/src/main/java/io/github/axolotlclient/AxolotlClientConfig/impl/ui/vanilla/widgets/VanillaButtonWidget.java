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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonWidget;
import net.minecraft.client.render.TextRenderer;

public class VanillaButtonWidget extends ButtonWidget {
	private static final Color DEFAULT = new Color(14737632);
	private static final Color DISABLED = new Color(10526880);
	private static final Color HOVERED = new Color(16777120);

	public VanillaButtonWidget(int x, int y, int width, int height, String message, PressAction action) {
		super(x, y, width, height, message, action);
	}

	@Override
	protected void drawWidget(int mouseX, int mouseY, float delta) {

		TextRenderer textRenderer = client.textRenderer;
		client.getTextureManager().bind(WIDGETS_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = active ? (hovered ? 2 : 1) : 0;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.blendFunc(770, 771);

		drawTexture(this.getX(), this.getY(), 0, 46 + k * 20, this.getWidth() / 2, this.getHeight());
		drawTexture(this.getX() + this.getWidth() / 2, this.getY(), 200 - this.getWidth() / 2, 46 + k * 20, this.getWidth() / 2, this.getHeight());
		Color l = DEFAULT;
		if (!this.active) {
			l = DISABLED;
		} else if (this.hovered) {
			l = HOVERED;
		}

		drawScrollingText(textRenderer, 2, l);
	}

	protected void drawScrollingText(TextRenderer renderer, int offset, Color color) {
		int left = getX() + offset;
		int right = getX()+getWidth() - offset;
		int center = getX() + getWidth()/2;

		drawScrollingText(renderer, getMessage(), center, left, getY(), right, getY()+getHeight(), color);
	}
}
