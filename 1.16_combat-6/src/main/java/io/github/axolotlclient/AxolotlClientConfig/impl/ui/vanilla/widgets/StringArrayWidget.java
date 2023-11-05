package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringArrayOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class StringArrayWidget extends VanillaButtonWidget {

	private final StringArrayOption option;

	public StringArrayWidget(int x, int y, int width, int height, StringArrayOption option) {
		super(x, y, width, height, new TranslatableText(option.get()), widget -> {
		});
		this.option = option;
	}

	@Override
	public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		if (!getMessage().getString().equals(new TranslatableText(option.get()).getString())) {
			setMessage(new TranslatableText(option.get()));
		}
		super.renderButton(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
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
		setMessage(new TranslatableText(option.get()));
	}
}