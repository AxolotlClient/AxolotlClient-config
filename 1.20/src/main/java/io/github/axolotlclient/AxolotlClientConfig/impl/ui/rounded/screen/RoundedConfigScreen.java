package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen;

import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonWidget;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;

public class RoundedConfigScreen extends Screen implements ConfigScreen, DrawingUtil {

	private final Screen parent;
	@Getter
	private final ConfigManager configManager;
	private final OptionCategory category;

	public RoundedConfigScreen(Screen parent, ConfigManager manager, OptionCategory category) {
		super(Text.translatable(manager.getRoot().getName()));
		this.parent = parent;
		this.configManager = manager;
		this.category = category;
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
	public void init() {
		addDrawableSelectableElement(new RoundedButtonListWidget(configManager, category, width, height, 45, height - 55, 25));
		addDrawableSelectableElement(new RoundedButtonWidget(width / 2 - 75, height - 40,
			CommonTexts.BACK, w -> closeScreen()));
	}

	@Override
	public void closeScreen() {
		client.setScreen(parent);
		configManager.save();
	}
}
