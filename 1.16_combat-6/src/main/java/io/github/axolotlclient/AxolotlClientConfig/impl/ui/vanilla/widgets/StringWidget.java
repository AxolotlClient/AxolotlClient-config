package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class StringWidget extends TextFieldWidget {

	private final StringOption option;

	public StringWidget(int x, int y, int width, int height, StringOption option) {
		super(MinecraftClient.getInstance().textRenderer, x, y, width, height, new TranslatableText(option.getName()));

		write(option.get());
		this.option = option;
		setChangedListener(option::set);
	}
}
