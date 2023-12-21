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

import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen.GraphicsEditorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class GraphicsWidget extends RoundedButtonWidget {
	private final GraphicsOption option;

	public GraphicsWidget(int x, int y, int width, int height, GraphicsOption option) {
		super(x, y, width, height, Text.translatable("open_editor"), buttonWidget -> {
		});
		this.option = option;
	}

	@Override
	public void onPress() {
		MinecraftClient.getInstance()
			.setScreen(new GraphicsEditorScreen(MinecraftClient.getInstance().currentScreen, option));
	}
}
