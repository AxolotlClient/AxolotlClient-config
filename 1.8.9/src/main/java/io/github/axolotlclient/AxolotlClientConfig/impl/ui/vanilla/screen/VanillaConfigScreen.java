package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Screen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class VanillaConfigScreen extends Screen {

	private final OptionCategory root;

	public VanillaConfigScreen(net.minecraft.client.gui.screen.Screen parent, OptionCategory root, String configName){
		super(I18n.translate(root.getName()), parent, configName);
		this.root = root;
	}

	@Override
	public void init() {
		super.init();

		ButtonListWidget widget = new VanillaButtonListWidget(client, width, height, 35, height-60, 25);
		addDrawableChild(widget);
		widget.addEntries(root);
		setFocusedChild(widget);

		addDrawableChild(new VanillaButtonWidget(width/2-75, height-40, 150, 20, I18n.translate("gui.back"),
			button -> MinecraftClient.getInstance().setScreen(parent)));
	}

	@Override
	public void render(long ctx, double mouseX, double mouseY, float delta) {
		client.textRenderer.draw(title, width/2 - client.textRenderer.getStringWidth(title)/2, 15, -1);
	}
}
