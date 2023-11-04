package io.github.axolotlclient.AxolotlClientConfig.impl;

import java.io.IOException;

import io.github.axolotlclient.AxolotlClientConfig.api.util.WindowPropertiesProvider;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class AxolotlClientConfigMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		ClientTickEvents.END_CLIENT_TICK.register(client -> AxolotlClientConfigImpl.getInstance().runTick());

		NVGMC.setWindowPropertiesProvider(new WindowPropertiesProvider() {
			@Override
			public int getHeight() {
				return MinecraftClient.getInstance().getWindow().getHeight();
			}

			@Override
			public int getWidth() {
				return MinecraftClient.getInstance().getWindow().getWidth();
			}

			@Override
			public float getScaleFactor() {
				return (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
			}
		});
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier("axolotlclientconfig", "resource_listener");
			}

			@Override
			public void apply(ResourceManager resourceManager) {
				//ConfigUI.getInstance().read(this.getClass().getResourceAsStream("assets/axolotlclientconfig/config.ui.json"));
				try {
					MinecraftClient.getInstance().getResourceManager()
						.getAllResources(new Identifier(ConfigUI.getInstance().getUiJsonPath())).forEach(resource -> {
						ConfigUI.getInstance().read(resource.getInputStream());
					});
				} catch (IOException ignored) {
				}
			}
		});
	}
}
