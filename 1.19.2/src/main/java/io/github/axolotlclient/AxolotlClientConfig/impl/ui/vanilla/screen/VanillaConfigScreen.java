package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.AbstractScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.MatrixStackProvider;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

public class VanillaConfigScreen extends AbstractScreen {

	private final OptionCategory root;

	public VanillaConfigScreen(Screen parent, OptionCategory root, String configName){
		super(I18n.translate(root.getName()), parent, configName);
		this.root = root;
	}

	@Override
	public void init() {
		super.init();

		ButtonListWidget widget = new VanillaButtonListWidget(client, width, height, 35, height-60, 25);
		addDrawableChild(widget);
		widget.addEntries(root);
		setFocused(widget);

		addDrawableChild(new VanillaButtonWidget(width/2-75, height-40, 150, 20, I18n.translate("gui.back"),
			button -> MinecraftClient.getInstance().setScreen(parent)));
	}

	@Override
	public void render(long ctx, double mouseX, double mouseY, float delta) {
		client.textRenderer.draw(MatrixStackProvider.getInstance().getStack(),
			title, width/2f - client.textRenderer.getWidth(title)/2f, 15, -1);
	}
}
