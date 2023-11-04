package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Screen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen.ColorSelectionScreen;
import net.minecraft.client.resource.language.I18n;

public class ColorWidget extends VanillaButtonWidget {
	private final ColorOption option;
	public ColorWidget(int x, int y, int width, int height, ColorOption option) {
		super(x, y, width, height, I18n.translate("open_selector"), widget -> {});
		this.option = option;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		client.setScreen(new ColorSelectionScreen((Screen) client.currentScreen, option));
	}
}
