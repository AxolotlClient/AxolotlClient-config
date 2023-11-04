package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen;

import java.io.IOException;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.AbstractScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGFont;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

public class RoundedConfigScreen extends AbstractScreen {

	private final OptionCategory root;
	public static NVGFont font;

	public RoundedConfigScreen(Screen parent, OptionCategory root, String configName){
		super(I18n.translate(root.getName()), parent, configName);
		this.root = root;
		this.enableVanillaRendering = false;
	}

	@Override
	public void init() {
		super.init();

		addDrawableChild(new RoundedButtonWidget(width/2-75, height-40, 150, 20, I18n.translate("gui.back"),
			button -> MinecraftClient.getInstance().setScreen(parent)));

		ButtonListWidget widget = new RoundedButtonListWidget(client, width, height, 35, height-60, 25);
		addDrawableChild(widget);
		widget.addEntries(root);
		//widget.setLeftPos(width/2-200);
		setFocusedChild(widget);
	}

	@Override
	public void render(long ctx, double mouseX, double mouseY, float delta) {
		fillRoundedRect(ctx, width / 2 - 200, 20, 400, height - 35, Colors.DARK_GRAY, 12);

		if (font == null) {
			try {
				font = NVGMC.createFont(this.getClass().getResourceAsStream("/Inter-Regular.ttf"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		drawCenteredString(ctx, font, title, width / 2f, 25, Colors.WHITE);
	}
}
