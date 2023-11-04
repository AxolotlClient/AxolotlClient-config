package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionCategoryImpl;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen.RoundedConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CategoryWidget extends RoundedButtonWidget {

	private final OptionCategory category;

	public CategoryWidget(int x, int y, int width, int height, OptionCategoryImpl category) {
		super(x, y, width, height, Text.translatable(category.getName()), widget -> {});
		this.category = category;
	}

	@Override
	public void onPress() {

		ConfigUI.getInstance().getCurrentStyle().createScreen().ifPresent(cl -> {
			try {
				if (MinecraftClient.getInstance().currentScreen != null) {
					/*Screen screen = (Screen) cl.getConstructor(Screen.class, OptionCategory.class, String.class)
						.newInstance(MinecraftClient.getInstance().currentScreen,
							category, ((ConfigScreen) MinecraftClient.getInstance().currentScreen).getConfigName());*/
					MinecraftClient.getInstance().setScreen(new RoundedConfigScreen(MinecraftClient.getInstance().currentScreen,
						category,((ConfigScreen) MinecraftClient.getInstance().currentScreen).getConfigName()));
				}
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		});
	}
}
