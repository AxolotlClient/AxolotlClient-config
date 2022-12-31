package io.github.axolotlclient.AxolotlClientConfig.common.types;

import io.github.axolotlclient.AxolotlClientConfig.common.AxolotlClientConfigManager;

public interface ConfigPart {
    AxolotlClientConfigManager getConfigManager();

    default String translate(String key){
        return getConfigManager().getTranslations().translate(key);
    }
}
