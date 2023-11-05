package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

public class EnumWidget<T extends Enum<T>> extends RoundedButtonWidget {
	private final EnumOption<T> option;

	public EnumWidget(int x, int y, int width, int height, EnumOption<T> option) {
		super(x, y, width, height, I18n.translate(String.valueOf(option.get())), widget -> {
		});
		this.option = option;
	}

	@Override
	public void onPress() {
		T[] values = option.getClazz().getEnumConstants();
		int i = 0;
		while (!values[i].equals(option.get())) {
			i += 1;
		}
		i += 1;
		if (i >= values.length) {
			i = 0;
		}
		option.set(values[i]);
		setMessage(I18n.translate(String.valueOf(option.get())));
	}
}