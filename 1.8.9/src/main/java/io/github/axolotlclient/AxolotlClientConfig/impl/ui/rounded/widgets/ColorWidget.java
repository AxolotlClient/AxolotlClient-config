package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen.ColorSelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

public class ColorWidget extends RoundedButtonWidget {
	private final ColorOption option;

	public ColorWidget(int x, int y, int width, int height, ColorOption option) {
		super(x, y, width, height, I18n.translate("open_selector"), widget -> {
		});
		this.option = option;
	}

	@Override
	public void onPress() {
		MinecraftClient.getInstance()
			.setScreen(new ColorSelectionScreen(MinecraftClient.getInstance().currentScreen, option));
	}
}
