package io.github.axolotlclient.config;

import io.github.axolotlclient.config.options.OptionCategory;

import java.util.List;

public abstract class ConfigHolder {

    public abstract List<OptionCategory> getCategories();
}
