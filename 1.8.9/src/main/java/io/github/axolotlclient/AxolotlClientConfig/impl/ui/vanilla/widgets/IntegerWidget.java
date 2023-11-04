package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;

public class IntegerWidget extends SliderWidget<IntegerOption, Integer> {
	public IntegerWidget(int x, int y, int width, int height, IntegerOption option) {
		super(x, y, width, height, option);
	}
}
