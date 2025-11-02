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

import java.io.IOException;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUIImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class AxolotlClientConfigMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> AxolotlClientConfigImpl.getInstance().runTick());

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public @NotNull Identifier getFabricId() {
				return Identifier.of("axolotlclientconfig", "resource_listener");
			}

			@Override
			public void reload(ResourceManager resourceManager) {
				ConfigUIImpl.getInstance().preReload();
				MinecraftClient.getInstance().getResourceManager()
					.getAllResources(Identifier.parse(ConfigUIImpl.getInstance().getUiJsonPath())).forEach(resource -> {
						try {
							ConfigUIImpl.getInstance().read(resource.open());
						} catch (IOException ignored) {
						}
					});
				ConfigUIImpl.getInstance().postReload();
			}
		});
	}
}
