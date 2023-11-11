package io.github.axolotlclient.AxolotlClientConfig.api.manager;

import java.util.Collection;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;

public interface ConfigManager {

	void save();

	void load();

	OptionCategory getRoot();

	Collection<String> getSuppressedNames();

	void suppressName(String name);
}
