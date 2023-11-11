package io.github.axolotlclient.AxolotlClientConfig.impl.util;

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
		return ConfigUI.getInstance().getScreen(ConfigStyles.class.getClassLoader(), configManager, category, parent);
	}

	@SuppressWarnings("unchecked")
	public <T extends Element & net.minecraft.client.gui.Drawable> T createWidget(int x, int y, int width, int height, WidgetIdentifieable option) {
		return (T) ConfigUI.getInstance().getWidget(x, y, width, height,
			option, ConfigStyles.class.getClassLoader());
	}
}
