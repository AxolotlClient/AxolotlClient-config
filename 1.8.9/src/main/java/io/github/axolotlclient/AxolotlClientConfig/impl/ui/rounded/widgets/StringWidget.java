package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.I18n;

public class StringWidget extends TextFieldWidget implements DrawingUtil {
	private final StringOption option;

	public StringWidget(int x, int y, int width, int height, StringOption option) {
		super(Minecraft.getInstance().textRenderer, x, y, width, height, I18n.translate(option.getName()));

		write(option.get());

		this.option = option;
		setChangedListener(option::set);
	}

	/*@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {
		if (!option.get().equals(getText())) {
			setText(option.get());
		}
		super.drawWidget(mouseX, mouseY, delta);
	}*/
}
