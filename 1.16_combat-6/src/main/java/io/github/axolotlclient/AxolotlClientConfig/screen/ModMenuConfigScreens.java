package io.github.axolotlclient.AxolotlClientConfig.screen;

import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

import java.util.HashMap;
import java.util.Map;

public class ModMenuConfigScreens implements ModMenuApi {

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> factories = new HashMap<>();
        AxolotlClientConfigManager.getInstance().getConfigs().forEach((s, configHolder) ->
                factories.put(s, parent ->
                        AxolotlClientConfigManager.getInstance().getConfigScreen(s, parent)));
        return factories;
    }
}
