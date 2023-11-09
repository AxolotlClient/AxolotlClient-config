package io.github.axolotlclient.AxolotlClientConfig.impl;

import java.io.IOException;

import io.github.axolotlclient.AxolotlClientConfig.api.util.WindowPropertiesProvider;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Window;
import net.minecraft.resource.Identifier;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import net.ornithemc.osl.resource.loader.api.ResourceLoaderEvents;

public class AxolotlClientConfigMod implements ClientModInitializer {

	@Override
	public void initClient() {
		MinecraftClientEvents.TICK_END.register(client -> AxolotlClientConfigImpl.getInstance().runTick());
		NVGMC.setWindowPropertiesProvider(new WindowPropertiesProvider() {
			@Override
			public int getHeight() {
				return Minecraft.getInstance().height;
			}

			@Override
			public int getWidth() {
				return Minecraft.getInstance().width;
			}

			@Override
			public float getScaleFactor() {
				return new Window(Minecraft.getInstance()).getScale();
			}
		});

		ResourceLoaderEvents.END_RESOURCE_RELOAD.register(() -> {
				ConfigUI.getInstance().preReload();
				try {
					Minecraft.getInstance().getResourceManager()
						.getResources(new Identifier(ConfigUI.getInstance().getUiJsonPath())).forEach(resource -> {
							ConfigUI.getInstance().read(resource.asStream());
						});
				} catch (IOException ignored) {
				}
				ConfigUI.getInstance().postReload();
		});
	}
}
