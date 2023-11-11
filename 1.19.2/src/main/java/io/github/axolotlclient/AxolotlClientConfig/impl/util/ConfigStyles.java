package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import java.lang.reflect.InvocationTargetException;

import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.options.WidgetIdentifieable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;

@UtilityClass
public class ConfigStyles {

	public Screen createScreen(Screen parent, ConfigManager manager, OptionCategory category) {
		return (Screen) ConfigUI.getInstance().getScreen(ConfigStyles.class.getClassLoader(), manager, category, parent);
	}

	public ClickableWidget createWidget(int x, int y, int width, int height, WidgetIdentifieable option) {
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
