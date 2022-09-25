package io.github.axolotlclient.AxolotlclientConfig;

import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;

import java.util.List;

/**
 * The class holding your config.
 * Put your options here, and return a list of them in the respective method.
 */

public abstract class ConfigHolder {

    /**
     * Return a list of your categories here. These will be used to generate the main page for your config
     * @return A list of your categories.
     */
    public abstract List<OptionCategory> getCategories();
}
