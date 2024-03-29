package io.github.axolotlclient.AxolotlClientConfig;

import io.github.axolotlclient.AxolotlClientConfig.common.ConfigHolder;
import io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlClientConfig.options.IntegerOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Config options for this config lib.
 * Add these options to your config to make them available to the user.
 * They universally are applied to every config screen created by this lib, but that shouldn't matter.
 */

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
