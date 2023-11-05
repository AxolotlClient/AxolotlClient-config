package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen.ColorSelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ColorWidget extends VanillaButtonWidget {
	private final ColorOption option;

	public ColorWidget(int x, int y, int width, int height, ColorOption option) {
		super(x, y, width, height, new TranslatableText("open_selector"), widget -> {
		});
		this.option = option;
	}

	@Override
	public void onPress() {
		MinecraftClient.getInstance()
			.openScreen(new ColorSelectionScreen(MinecraftClient.getInstance().currentScreen, option));
	}
}