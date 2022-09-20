package io.github.axolotlclient.config;

import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.OptionCategory;

import java.util.ArrayList;
import java.util.List;

public class AxolotlClientConfigConfig extends ConfigHolder {

    public static BooleanOption showQuickToggles = new BooleanOption("showQuickToggles", true);
    public static final BooleanOption showOptionTooltips = new BooleanOption("showOptionTooltips", true);
    public static final BooleanOption showCategoryTooltips = new BooleanOption("showCategoryTooltips", false);

    public static final BooleanOption searchIgnoreCase = new BooleanOption("searchIgnoreCase", true);
    public static final BooleanOption searchSort = new BooleanOption("searchSort", true);
    public static final EnumOption searchSortOrder = new EnumOption("searchSortOrder", new String[]{"ASCENDING", "DESCENDING"}, "ASCENDING");
    public static final BooleanOption searchForOptions = new BooleanOption("searchForOptions", false);
    public static final IntegerOption chromaSpeed = new IntegerOption("chromaSpeed", 5, 1, 20);

    @Override
    public List<OptionCategory> getCategories() {
        return new ArrayList<>();
    }
}
