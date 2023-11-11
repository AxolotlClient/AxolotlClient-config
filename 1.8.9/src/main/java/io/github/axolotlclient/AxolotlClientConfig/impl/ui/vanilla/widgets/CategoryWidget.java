package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionCategoryImpl;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.I18n;

public class CategoryWidget extends VanillaButtonWidget {

	private final OptionCategory category;

	public CategoryWidget(int x, int y, int width, int height, OptionCategoryImpl category) {
		super(x, y, width, height, I18n.translate(category.getName()), widget -> {
		});
		this.category = category;
	}

	@Override
	public void onPress() {
		if (Minecraft.getInstance().screen != null) {
			Minecraft.getInstance().openScreen(
				ConfigStyles.createScreen(Minecraft.getInstance().screen,
					((ConfigScreen) Minecraft.getInstance().screen).getConfigManager(), category));
		}


	}
}
