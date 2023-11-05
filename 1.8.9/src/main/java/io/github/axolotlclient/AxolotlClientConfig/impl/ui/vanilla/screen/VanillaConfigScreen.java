package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

public class VanillaConfigScreen extends io.github.axolotlclient.AxolotlClientConfig.impl.ui.Screen implements ConfigScreen {
	private final Screen parent;
	@Getter
	private final String configName;
	private final OptionCategory root;

	public VanillaConfigScreen(Screen parent, OptionCategory root, String configName) {
		super(root.getName());
		this.parent = parent;
		this.configName = configName;
		this.root = root;
	}

	@Override
	public void init() {
		addDrawableChild(new VanillaButtonListWidget(root, width, height, 45, height - 55, 25));
		addDrawableChild(new VanillaButtonWidget(width / 2 - 75, height - 45, 150, 20,
			I18n.translate("gui.back"), w -> MinecraftClient.getInstance().setScreen(parent)));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		super.render(mouseX, mouseY, delta);

		drawCenteredString(MinecraftClient.getInstance().textRenderer, getTitle(), width / 2, 25, -1);
	}

	@Override
	public void removed() {
		AxolotlClientConfig.getInstance().getConfigManager(configName).save();
	}
}
