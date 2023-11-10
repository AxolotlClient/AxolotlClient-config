package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

public class RoundedConfigScreen extends io.github.axolotlclient.AxolotlClientConfig.impl.ui.Screen implements ConfigScreen, DrawingUtil {

	private final Screen parent;
	private final String configName;
	private final OptionCategory root;

	public RoundedConfigScreen(Screen parent, OptionCategory root, String configName) {
		super(I18n.translate(root.getName()));
		this.parent = parent;
		this.configName = configName;
		this.root = root;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		NVGUtil.wrap(ctx -> {
			fillRoundedRect(ctx, 15, 15, width - 30, height - 30, Colors.DARK_GRAY, 12);
			drawCenteredString(ctx, NVGHolder.getFont(), getTitle(), width / 2f, 25, Colors.WHITE);
			NVGHolder.setContext(ctx);
			super.render(mouseX, mouseY, delta);
		});
	}

	@Override
	public String getConfigName() {
		return configName;
	}

	@Override
	public void init() {
		addDrawableChild(new RoundedButtonListWidget(root, width, height, 45, height - 55, 25));
		addDrawableChild(new RoundedButtonWidget(width / 2 - 75, height - 40,
			I18n.translate("gui.back"), w -> Minecraft.getInstance().openScreen(parent)));
	}

	@Override
	public void removed() {
		AxolotlClientConfig.getInstance().getConfigManager(getConfigName()).save();
	}
}
