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

package io.github.axolotlclient.AxolotlClientConfig.impl;

import io.github.axolotlclient.AxolotlClientConfig.api.util.WindowPropertiesProvider;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class AxolotlClientConfigRoundedStyleMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		NVGMC.setWindowPropertiesProvider(new WindowPropertiesProvider() {
			@Override
			public int getHeight() {
				return MinecraftClient.getInstance().getFramebuffer().textureHeight;
			}

			@Override
			public int getWidth() {
				return MinecraftClient.getInstance().getFramebuffer().textureWidth;
			}

			@Override
			public float getScaleFactor() {
				return (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
			}
		});
	}
}
