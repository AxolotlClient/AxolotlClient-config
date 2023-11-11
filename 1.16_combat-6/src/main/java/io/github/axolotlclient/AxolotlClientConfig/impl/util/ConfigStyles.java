package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import java.lang.reflect.InvocationTargetException;

import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.options.WidgetIdentifieable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

@UtilityClass
public class ConfigStyles {

	public Screen createScreen(Screen parent, ConfigManager configManager, OptionCategory category) {
		return (Screen) ConfigUI.getInstance().getScreen(ConfigStyles.class.getClassLoader(), configManager, category, parent);
	}

	@SuppressWarnings("unchecked")
	public <T extends Element & net.minecraft.client.gui.Drawable> T createWidget(int x, int y, int width, int height, WidgetIdentifieable option) {
		try {
			return (T) ConfigUI.getInstance().getWidget(option.getWidgetIdentifier(), ConfigStyles.class.getClassLoader())
				.getConstructor(int.class, int.class, int.class, int.class, option.getClass())
				.newInstance(x, y, width, height, option);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
	}
}
