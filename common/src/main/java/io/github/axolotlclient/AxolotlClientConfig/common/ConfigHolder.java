package io.github.axolotlclient.AxolotlClientConfig.common;

import io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * The class holding your config.
 * Put your options here, and return a list of them in the respective method.
 */

public abstract class ConfigHolder {

    public static final ConfigHolder EMPTY = new ConfigHolder() {
        @Override
        public List<OptionCategory> getCategories() {
            return new ArrayList<>();
        }
    };

    /**
     * Return a list of your categories here. These will be used to generate the main page for your config
     * @return A list of your categories.
     */
    public abstract List<OptionCategory> getCategories();
}
