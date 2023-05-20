package io.github.axolotlclient.AxolotlClientConfig.screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;

import java.util.HashMap;
import java.util.Map;

public class ModMenuConfigScreens implements ModMenuApi {

	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		HashMap<String, ConfigScreenFactory<?>> factories = new HashMap<>();
		AxolotlClientConfigManager.getInstance().getConfigs().forEach((s, configHolder) ->
			factories.put(s, parent ->
				AxolotlClientConfigManager.getInstance().getConfigScreen(s, parent)));
		return factories;
	}
}
