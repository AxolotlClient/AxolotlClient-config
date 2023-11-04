package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;

public class ResetButtonWidget extends VanillaButtonWidget {

	private final Option<?> option;
	public ResetButtonWidget(int x, int y, int width, int height, Option<?> option) {
		super(x, y, width, height, I18n.translate("action.reset"), widget -> {
			option.setDefault();
			Window window = MinecraftClient.getInstance().getWindow();
			int i = window.getWidth();
			int j = window.getHeight();
			MinecraftClient.getInstance().currentScreen.init(MinecraftClient.getInstance(), i, j);
		});
		this.option = option;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.active = !option.getDefault().equals(option.get());
		super.render(mouseX, mouseY, delta);
	}
}
