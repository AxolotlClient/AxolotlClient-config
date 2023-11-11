package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.options.WidgetIdentifieable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ClickableWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.screen.Screen;

@UtilityClass
public class ConfigStyles {

	public Screen createScreen(Screen parent, ConfigManager manager, OptionCategory category) {
		return ConfigUI.getInstance().getScreen(ConfigStyles.class.getClassLoader(), manager, category, parent);
	}

	public ClickableWidget createWidget(int x, int y, int width, int height, WidgetIdentifieable option) {
		return (ClickableWidget) ConfigUI.getInstance().getWidget(x, y, width, height,
			option, ConfigStyles.class.getClassLoader());
	}
}
