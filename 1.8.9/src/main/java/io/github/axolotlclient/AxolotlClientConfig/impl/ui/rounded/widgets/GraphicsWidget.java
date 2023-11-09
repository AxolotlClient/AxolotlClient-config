package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen.GraphicsEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.I18n;

public class GraphicsWidget extends RoundedButtonWidget {
	private final GraphicsOption option;

	public GraphicsWidget(int x, int y, int width, int height, GraphicsOption option) {
		super(x, y, width, height, I18n.translate("open_editor"), buttonWidget -> {
		});
		this.option = option;
	}

	@Override
	public void onPress() {
		Minecraft.getInstance()
			.openScreen(new GraphicsEditorScreen(Minecraft.getInstance().screen, option));
	}
}
