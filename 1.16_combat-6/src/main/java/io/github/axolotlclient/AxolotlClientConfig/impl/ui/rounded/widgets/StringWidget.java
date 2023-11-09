package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class StringWidget extends TextFieldWidget implements DrawingUtil {
	private final StringOption option;

	public StringWidget(int x, int y, int width, int height, StringOption option) {
		super(MinecraftClient.getInstance().textRenderer, x, y, width, height, new TranslatableText(option.getName()));

		write(option.get());

		this.option = option;
		setChangedListener(option::set);
	}

	@Override
	public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		if (!option.get().equals(getText())) {
			setText(option.get());
		}
		super.renderButton(graphics, mouseX, mouseY, delta);
	}
}
