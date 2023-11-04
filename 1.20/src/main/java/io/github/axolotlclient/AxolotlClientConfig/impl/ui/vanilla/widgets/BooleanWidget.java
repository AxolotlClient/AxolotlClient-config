package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.CommonTexts;

public class BooleanWidget extends ButtonWidget implements Updatable {

	private final BooleanOption option;

	public BooleanWidget(int x, int y, int width, int height, BooleanOption option) {
		super(x, y, width, height, option.get() ? CommonTexts.ON : CommonTexts.OFF, widget -> {
			option.set(!option.get());
			widget.setMessage(option.get() ? CommonTexts.ON : CommonTexts.OFF);
		}, DEFAULT_NARRATION);
		this.option = option;
	}

	public void update() {
		setMessage(option.get() ? CommonTexts.ON : CommonTexts.OFF);
	}
}
