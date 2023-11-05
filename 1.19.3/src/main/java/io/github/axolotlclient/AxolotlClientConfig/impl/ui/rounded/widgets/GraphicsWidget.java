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
