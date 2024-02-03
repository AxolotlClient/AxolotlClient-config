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

import com.google.common.util.concurrent.AtomicDouble;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class ResetButtonWidget<T> extends VanillaButtonWidget {

	private final Option<T> option;

	public ResetButtonWidget(int x, int y, int width, int height, Option<T> option) {
		super(x, y, width, height, new TranslatableText("action.reset"), widget -> {
			option.set(option.getDefault());
			Window window = MinecraftClient.getInstance().getWindow();
			int i = window.getScaledWidth();
			int j = window.getScaledHeight();
			Screen current = MinecraftClient.getInstance().currentScreen;
			if (current != null) {
				AtomicDouble scroll = new AtomicDouble();
				current.children().stream()
					.filter(e -> e instanceof VanillaButtonListWidget)
					.map(e -> (VanillaButtonListWidget) e).findFirst().ifPresent(list -> {
						scroll.set(list.getScrollAmount());
					});
				current.init(MinecraftClient.getInstance(), i, j);
				current.children().stream()
					.filter(e -> e instanceof VanillaButtonListWidget)
					.map(e -> (VanillaButtonListWidget) e).findFirst().ifPresent(list -> {
						list.setScrollAmount(scroll.get());
					});
			}
		});
		this.option = option;
	}

	@Override
	public void render(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		this.active = !option.getDefault().equals(option.get());
		super.render(graphics, mouseX, mouseY, delta);
	}
}
