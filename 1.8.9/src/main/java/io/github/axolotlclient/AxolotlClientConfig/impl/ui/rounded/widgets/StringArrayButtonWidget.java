package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringArrayOption;
import net.minecraft.client.resource.language.I18n;

public class StringArrayButtonWidget extends RoundedButtonWidget {

	private final StringArrayOption option;

	public StringArrayButtonWidget(int x, int y, int width, int height, StringArrayOption option) {
		super(x, y, width, height, I18n.translate(option.get()), widget -> {
		});
		this.option = option;
	}

	@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {
		if (!getMessage().equals(I18n.translate(option.get()))) {
			setMessage(I18n.translate(option.get()));
		}
		super.drawWidget(mouseX, mouseY, delta);
	}

	@Override
	public void onPress() {
		String[] values = option.getValues();
		int i = 0;
		while (!values[i].equals(option.get())) {
			i += 1;
		}
		i += 1;
		if (i >= values.length) {
			i = 0;
		}
		option.set(values[i]);
		setMessage(I18n.translate(option.get()));
	}
}
