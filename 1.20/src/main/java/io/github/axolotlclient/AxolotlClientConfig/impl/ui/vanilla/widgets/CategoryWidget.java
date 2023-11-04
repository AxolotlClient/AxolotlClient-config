package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import java.lang.reflect.InvocationTargetException;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionCategoryImpl;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.Text;

public class CategoryWidget extends ButtonWidget {

	private final OptionCategory category;
	public CategoryWidget(int x, int y, int width, int height, OptionCategoryImpl category) {
		super(x, y, width, height, Text.translatable(category.getName()), widget ->{}, DEFAULT_NARRATION);
		this.category = category;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {

		ConfigUI.getInstance().getCurrentStyle().createScreen().ifPresent(cl -> {
			try {
				if (MinecraftClient.getInstance().currentScreen != null) {
					Screen screen = (Screen) cl.getConstructor(Screen.class, OptionCategory.class, String.class)
						.newInstance(MinecraftClient.getInstance().currentScreen, category,
							((ConfigScreen) MinecraftClient.getInstance().currentScreen).getConfigName());
					MinecraftClient.getInstance().setScreen(screen);
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
					 NoSuchMethodException e) {
				throw new IllegalStateException(e);
			}
		});
	}
}
