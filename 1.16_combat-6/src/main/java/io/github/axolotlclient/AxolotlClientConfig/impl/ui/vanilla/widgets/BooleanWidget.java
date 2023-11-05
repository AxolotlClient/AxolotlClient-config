package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import net.minecraft.client.gui.screen.ScreenTexts;

public class BooleanWidget extends VanillaButtonWidget implements Updatable {

	private final BooleanOption option;

	public BooleanWidget(int x, int y, int width, int height, BooleanOption option) {
		super(x, y, width, height, option.get() ? ScreenTexts.ON : ScreenTexts.OFF, widget -> {
			option.set(!option.get());
			widget.setMessage(option.get() ? ScreenTexts.ON : ScreenTexts.OFF);
		});
		this.option = option;
	}

	public void update() {
		setMessage(option.get() ? ScreenTexts.ON : ScreenTexts.OFF);
	}
}
