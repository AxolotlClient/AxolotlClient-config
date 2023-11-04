package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import net.minecraft.client.resource.language.I18n;

public class BooleanWidget extends RoundedButtonWidget {

	private final BooleanOption option;

	public BooleanWidget(int x, int y, int width, int height, BooleanOption option) {
		super(x, y, width, height, option.get() ? I18n.translate("options.on") : I18n.translate("options.off"), button -> {
			option.set(!option.get());
			button.setMessage(option.get() ? I18n.translate("options.on") : I18n.translate("options.off"));
		});
		this.option = option;
	}

	@Override
	protected void drawWidget(long ctx, int mouseX, int mouseY, float delta) {
		if (getMessage().equals(I18n.translate("options.on")) != option.get()) {
			setMessage(option.get() ? I18n.translate("options.on") : I18n.translate("options.off"));
		}
		super.drawWidget(ctx, mouseX, mouseY, delta);
	}
}
