package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen.GraphicsEditorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

public class GraphicsWidget extends RoundedButtonWidget {
	private final GraphicsOption option;

	public GraphicsWidget(int x, int y, int width, int height, GraphicsOption option) {
		super(x, y, width, height, new TranslatableText("open_editor"), buttonWidget -> {
		});
		this.option = option;
	}

	@Override
	public void onPress() {
		MinecraftClient.getInstance()
			.openScreen(new GraphicsEditorScreen(MinecraftClient.getInstance().currentScreen, option));
	}
}
