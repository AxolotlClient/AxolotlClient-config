package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionCategoryImpl;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CategoryWidget extends RoundedButtonWidget {

	private final OptionCategory category;

	public CategoryWidget(int x, int y, int width, int height, OptionCategoryImpl category) {
		super(x, y, width, height, new TranslatableText(category.getName()), widget -> {
		});
		this.category = category;
	}

	@Override
	public void onPress() {
		if (MinecraftClient.getInstance().currentScreen != null) {
			MinecraftClient.getInstance().openScreen(
				ConfigStyles.createScreen(MinecraftClient.getInstance().currentScreen,
					category, ((ConfigScreen) MinecraftClient.getInstance().currentScreen).getConfigName()));
		}
	}
}
