package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.text.Text;

public class StringWidget extends TextFieldWidget implements DrawingUtil {
	private final StringOption option;

	public StringWidget(int x, int y, int width, int height, StringOption option) {
		super(MinecraftClient.getInstance().textRenderer, x, y, width, height, Text.translatable(option.getName()));

		write(option.get());

		this.option = option;
		setChangedListener(option::set);
	}

	@Override
	public void drawWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (!option.get().equals(getText())) {
			setText(option.get());
		}
		super.drawWidget(graphics, mouseX, mouseY, delta);
	}
}
