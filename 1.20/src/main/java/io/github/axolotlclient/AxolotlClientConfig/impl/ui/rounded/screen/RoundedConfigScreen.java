package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;

public class RoundedConfigScreen extends Screen implements ConfigScreen, DrawingUtil {

	private final Screen parent;
	private final String configName;
	private final OptionCategory root;

	public RoundedConfigScreen(Screen parent, OptionCategory root, String configName) {
		super(Text.translatable(root.getName()));
		this.parent = parent;
		this.configName = configName;
		this.root = root;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		NVGMC.wrap(ctx -> {
			fillRoundedRect(ctx, 15, 15, width - 30, height - 30, Colors.DARK_GRAY, 12);
			drawCenteredString(ctx, NVGHolder.getFont(), getTitle().getString(), width / 2f, 25, Colors.WHITE);
			NVGHolder.setContext(ctx);
			super.render(graphics, mouseX, mouseY, delta);
		});
	}

	@Override
	public String getConfigName() {
		return configName;
	}

	@Override
	public void init() {
		addDrawableSelectableElement(new RoundedButtonListWidget(root, width, height, 45, height - 55, 25));
		addDrawableSelectableElement(new RoundedButtonWidget(width / 2 - 75, height - 40,
			CommonTexts.BACK, w -> closeScreen()));
	}

	@Override
	public void closeScreen() {
		client.setScreen(parent);
		AxolotlClientConfig.getInstance().getConfigManager(getConfigName()).save();
	}
}
