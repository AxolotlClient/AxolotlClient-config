package io.github.axolotlclient.AxolotlClientConfig.impl;

import java.io.IOException;

import io.github.axolotlclient.AxolotlClientConfig.api.util.WindowPropertiesProvider;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import net.fabricmc.api.ClientModInitializer;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.legacyfabric.fabric.api.resource.IdentifiableResourceReloadListener;
import net.legacyfabric.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class AxolotlClientConfigMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> AxolotlClientConfigImpl.getInstance().runTick());

		NVGMC.setWindowPropertiesProvider(new WindowPropertiesProvider() {
			@Override
			public int getHeight() {
				return MinecraftClient.getInstance().height;
			}

			@Override
			public int getWidth() {
				return MinecraftClient.getInstance().width;
			}

			@Override
			public float getScaleFactor() {
				return new Window(MinecraftClient.getInstance()).getScaleFactor();
			}
		});

		ResourceManagerHelper.getInstance().registerReloadListener(new IdentifiableResourceReloadListener() {
			@Override
			public net.legacyfabric.fabric.api.util.Identifier getFabricId() {
				return new net.legacyfabric.fabric.api.util.Identifier("axolotlclientconfig", "resource_listener");
			}

			@Override
			public void reload(ResourceManager resourceManager) {
				ConfigUI.getInstance().preReload();
				try {
					MinecraftClient.getInstance().getResourceManager()
						.getAllResources(new Identifier(ConfigUI.getInstance().getUiJsonPath())).forEach(resource -> {
							ConfigUI.getInstance().read(resource.getInputStream());
						});
				} catch (IOException ignored) {
				}
				ConfigUI.getInstance().postReload();
			}
		});
	}
}
