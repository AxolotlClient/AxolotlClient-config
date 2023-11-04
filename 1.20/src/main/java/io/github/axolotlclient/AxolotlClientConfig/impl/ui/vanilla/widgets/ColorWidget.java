package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen.ColorSelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.Text;

public class ColorWidget extends ButtonWidget {
	private final ColorOption option;

	public ColorWidget(int x, int y, int width, int height, ColorOption option) {
		super(x, y, width, height, Text.translatable("open_selector"), widget -> {
		}, DEFAULT_NARRATION);
		this.option = option;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		MinecraftClient.getInstance()
			.setScreen(new ColorSelectionScreen(MinecraftClient.getInstance().currentScreen, option));
	}
}
