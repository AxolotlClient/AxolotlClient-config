package io.github.axolotlclient.AxolotlClientConfig.api;

import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.AxolotlClientConfigImpl;

public interface AxolotlClientConfig {

	static AxolotlClientConfig getInstance(){
		return AxolotlClientConfigImpl.getInstance();
	}

	void register(ConfigManager manager);

	default void getConfigManager(OptionCategory root){

	}

	ConfigManager getConfigManager(String name);
}
