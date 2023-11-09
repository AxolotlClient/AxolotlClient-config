package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.TextFieldWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.I18n;

public class StringWidget extends TextFieldWidget {

	private final StringOption option;

	public StringWidget(int x, int y, int width, int height, StringOption option) {
		super(Minecraft.getInstance().textRenderer, x, y, width, height, I18n.translate(option.getName()));

		write(option.get());
		this.option = option;
		setChangedListener(option::set);
	}
}
