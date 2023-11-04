package io.github.axolotlclient.AxolotlClientConfig.impl;

import java.io.IOException;

import io.github.axolotlclient.AxolotlClientConfig.api.util.WindowPropertiesProvider;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

public class AxolotlClientConfigMod implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		ClientTickEvents.END.register(client -> AxolotlClientConfigImpl.getInstance().runTick());

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

		ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(new SimpleSynchronousResourceReloader() {
			@Override
			public @NotNull Identifier getQuiltId() {
				return new Identifier("axolotlclientconfig", "resource_listener");
			}

			@Override
			public void reload(ResourceManager resourceManager) {
				ConfigUI.getInstance().clearStyles();
				MinecraftClient.getInstance().getResourceManager()
					.getAllResources(new Identifier(ConfigUI.getInstance().getUiJsonPath())).forEach(resource -> {
						try {
							ConfigUI.getInstance().read(resource.open());
						} catch (IOException ignored) {
						}
					});
			}
		});
	}
}
