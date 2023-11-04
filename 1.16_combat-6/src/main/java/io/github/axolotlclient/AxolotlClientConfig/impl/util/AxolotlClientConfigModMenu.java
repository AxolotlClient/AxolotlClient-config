package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.AxolotlClientConfigImpl;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class AxolotlClientConfigModMenu implements ModMenuApi {

	/**
	 * Used to provide config screen factories for other mods. This takes second
	 * priority to a mod's own config screen factory provider. For example, if
	 * mod `xyz` supplies a config screen factory, mod `abc` providing a config
	 * screen to `xyz` will be pointless, as the one provided by `xyz` will be
	 * used.
	 * <p>
	 * This method is NOT meant to be used to add a config screen factory to
	 * your own mod.
	 *
	 * @return a map of mod ids to screen factories.
	 */
	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		Map<String, ConfigScreenFactory<?>> map = new HashMap<>();
		AxolotlClientConfigImpl.getInstance().getRegisteredManagers().forEach((s, manager) -> {
			map.put(s, parent -> {
				try {
					return (Screen) Class.forName(ConfigUI.getInstance().getCurrentStyle().getScreen())
						.getConstructor(Screen.class, OptionCategory.class, String.class).newInstance(parent, manager.getRoot(), s);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
						 NoSuchMethodException | ClassNotFoundException e) {
					throw new IllegalStateException(e);
				}
			});
		});
		return map;
	}
}
