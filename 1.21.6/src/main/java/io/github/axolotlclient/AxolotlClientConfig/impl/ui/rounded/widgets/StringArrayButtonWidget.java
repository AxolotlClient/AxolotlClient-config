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

import com.mojang.blaze3d.platform.InputConstants;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringArrayOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.ArrayUtils;

public class StringArrayButtonWidget extends RoundedButtonWidget {

	private final StringArrayOption option;

	public StringArrayButtonWidget(int x, int y, int width, int height, StringArrayOption option) {
		super(x, y, width, height, Component.translatable(option.get()), widget -> {
		});
		this.option = option;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (!getMessage().getString().equals(I18n.get(option.get()))) {
			setMessage(Component.translatable(option.get()));
		}
		super.renderWidget(graphics, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				boolean bl = this.isMouseOver(mouseX, mouseY);
				if (bl) {
					this.playDownSound(Minecraft.getInstance().getSoundManager());
					this.cycle(button == 0 ? 1 : -1, true);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.active || !this.visible) {
			return false;
		} else if (CommonInputs.selected(keyCode) || keyCode == InputConstants.KEY_RIGHT) {
			if (this.cycle(1, false)) {
				this.playDownSound(Minecraft.getInstance().getSoundManager());
				return true;
			}
		} else if (keyCode == InputConstants.KEY_LEFT) {
			if (this.cycle(-1, false)) {
				this.playDownSound(Minecraft.getInstance().getSoundManager());
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean isValidClickButton(int button) {
		return button == 0 || button == 1;
	}

	private boolean cycle(int step, boolean wrap) {
		if (Screen.hasShiftDown()) {
			step *= -1;
		}
		String[] values = option.getValues();
		int i = ArrayUtils.indexOf(values, option.get());
		if (!wrap && (i == 0 || i == values.length-1)) {
			return false;
		}
		option.set(values[Math.floorMod(i + step, values.length)]);
		setMessage(Component.translatable(String.valueOf(option.get())));
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (scrollY > 0.0) {
			this.cycle(-1, true);
		} else if (scrollY < 0.0) {
			this.cycle(1, true);
		}

		return true;
	}
}
