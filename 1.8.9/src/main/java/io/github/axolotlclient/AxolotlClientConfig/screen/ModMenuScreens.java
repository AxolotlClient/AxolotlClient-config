package io.github.axolotlclient.AxolotlClientConfig.screen;

import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuScreens implements ModMenuApi {

    @Override
    public String getModId() {
        return "axolotlclientconfig";
    }

    /*@Override // For when 1.8.9 has a modmenu version that supports crazy shit like this
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        HashMap<String, ConfigScreenFactory<?>> factories = new HashMap<>();
        AxolotlClientConfigManager.getConfigs().forEach((s, configHolder) ->
                factories.put(s, (parent) ->
                        AxolotlClient.getConfigScreen(s, parent));
        return factories;
    }*/
}
