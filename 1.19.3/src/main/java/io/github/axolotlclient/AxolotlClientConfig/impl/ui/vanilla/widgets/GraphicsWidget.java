package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen.GraphicsEditorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class GraphicsWidget extends VanillaButtonWidget {
	private final GraphicsOption option;

	public GraphicsWidget(int x, int y, int width, int height, GraphicsOption option) {
		super(x, y, width, height, Text.translatable("open_editor"), buttonWidget -> {
		}, DEFAULT_NARRATION);
		this.option = option;
	}

	@Override
	public void onPress() {
		MinecraftClient.getInstance()
			.setScreen(new GraphicsEditorScreen(MinecraftClient.getInstance().currentScreen, option));
	}
}
