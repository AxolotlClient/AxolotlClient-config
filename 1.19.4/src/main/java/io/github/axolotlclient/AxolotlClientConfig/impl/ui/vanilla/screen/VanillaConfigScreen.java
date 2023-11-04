package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaEntryListWidget;
import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;

public class VanillaConfigScreen extends Screen {
	private final Screen parent;
	@Getter
	private final String configName;
	private final OptionCategory root;

	public VanillaConfigScreen(Screen parent, OptionCategory root, String configName) {
		super(Text.translatable(root.getName()));
		this.parent = parent;
		this.configName = configName;
		this.root = root;
	}

	@Override
	protected void init() {
		addDrawableChild(new VanillaEntryListWidget(root, width, height, 45, height - 55, 25));
		addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, w -> client.setScreen(parent))
			.position(width / 2 - 75, height - 45).build());
	}

	@Override
	public void render(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, delta);

		drawCenteredText(graphics, client.textRenderer, getTitle(), width / 2, 25, -1);
	}

	@Override
	public void removed() {
		AxolotlClientConfig.getInstance().getConfigManager(configName).save();
	}
}
