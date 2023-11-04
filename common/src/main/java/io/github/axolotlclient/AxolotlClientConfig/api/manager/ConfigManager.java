package io.github.axolotlclient.AxolotlClientConfig.api.manager;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;

public interface ConfigManager {

	void save();

	void load();

	OptionCategory getRoot();
}
