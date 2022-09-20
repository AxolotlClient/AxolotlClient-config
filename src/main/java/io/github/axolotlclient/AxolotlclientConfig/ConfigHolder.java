package io.github.axolotlclient.AxolotlclientConfig;

import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;

import java.util.List;

public abstract class ConfigHolder {

    public abstract List<OptionCategory> getCategories();
}
