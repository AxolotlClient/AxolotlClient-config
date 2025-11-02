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

import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CommonInputs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

public class EnumWidget<T extends Enum<T>> extends RoundedButtonWidget {
	private final EnumOption<T> option;

	public EnumWidget(int x, int y, int width, int height, EnumOption<T> option) {
		super(x, y, width, height, Text.translatable(String.valueOf(option.get())), widget -> {
		});
		this.option = option;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				boolean bl = this.isMouseOver(mouseX, mouseY);
				if (bl) {
					this.playDownSound(MinecraftClient.getInstance().getSoundManager());
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
		} else if (CommonInputs.isToggle(keyCode) || keyCode == GLFW.GLFW_KEY_RIGHT) {
			if (this.cycle(1, false)) {
				this.playDownSound(MinecraftClient.getInstance().getSoundManager());
				return true;
			}
		} else if(keyCode == GLFW.GLFW_KEY_LEFT) {
			if (this.cycle(-1, false)) {
				this.playDownSound(MinecraftClient.getInstance().getSoundManager());
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
		T[] values = option.getClazz().getEnumConstants();
		int i = ArrayUtils.indexOf(values, option.get());
		if (!wrap && (i == 0 || i == values.length-1)) {
			return false;
		}
		option.set(values[Math.floorMod(i + step, values.length)]);
		setMessage(Text.translatable(String.valueOf(option.get())));
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
