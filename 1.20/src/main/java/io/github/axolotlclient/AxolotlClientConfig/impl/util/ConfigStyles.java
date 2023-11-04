package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import java.lang.reflect.InvocationTargetException;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;

@UtilityClass
public class ConfigStyles {

	public Screen createScreen(Screen parent, OptionCategory root, String configName) {
		try {
			return (Screen) ConfigUI.getInstance().getScreen(ConfigStyles.class.getClassLoader())
				.getConstructor(Screen.class, OptionCategory.class, String.class)
				.newInstance(parent, root, configName);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
	}

	public ClickableWidget createWidget(int x, int y, int width, int height, Option<?> option) {
		try {
			return (ClickableWidget) ConfigUI.getInstance().getWidget(option.getWidgetIdentifier(), ConfigStyles.class.getClassLoader())
				.getConstructor(int.class, int.class, int.class, int.class, option.getClass())
				.newInstance(x, y, width, height, option);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
	}
}
