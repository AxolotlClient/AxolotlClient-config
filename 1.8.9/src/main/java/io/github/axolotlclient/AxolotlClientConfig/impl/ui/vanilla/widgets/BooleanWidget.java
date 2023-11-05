package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import net.minecraft.client.resource.language.I18n;

public class BooleanWidget extends VanillaButtonWidget implements Updatable {

	private final BooleanOption option;

	public BooleanWidget(int x, int y, int width, int height, BooleanOption option) {
		super(x, y, width, height, option.get() ? I18n.translate("options.on") : I18n.translate("options.off"), widget -> {
			option.set(!option.get());
			widget.setMessage(option.get() ? I18n.translate("options.on") : I18n.translate("options.off"));
		});
		this.option = option;
	}

	public void update() {
		setMessage(option.get() ? I18n.translate("options.on") : I18n.translate("options.off"));
	}
}
