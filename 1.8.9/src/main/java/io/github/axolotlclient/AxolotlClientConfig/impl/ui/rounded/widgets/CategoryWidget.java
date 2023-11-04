package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import java.lang.reflect.InvocationTargetException;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionCategoryImpl;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.AbstractScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen.RoundedConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

public class CategoryWidget extends RoundedButtonWidget {

	private final OptionCategory category;

	public CategoryWidget(int x, int y, int width, int height, OptionCategoryImpl category) {
		super(x, y, width, height, I18n.translate(category.getName()), widget -> {});
		this.category = category;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {

		ConfigUI.getInstance().getCurrentStyle().createScreen().ifPresent(cl -> {
			try {
				if (client.currentScreen != null) {
					Screen screen = (Screen) cl.getConstructor(Screen.class, OptionCategory.class, String.class)
						.newInstance(client.currentScreen, category, ((AbstractScreen) client.currentScreen).getConfigName());
					client.setScreen(screen);
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
					 NoSuchMethodException e) {
				throw new IllegalStateException(e);
			}
		});
	}
}
