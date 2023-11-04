package io.github.axolotlclient.AxolotlClientConfig.api.options;

import java.util.Collection;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionCategoryImpl;

public interface OptionCategory extends WidgetIdentifieable {

	static OptionCategory create(String name){
		return new OptionCategoryImpl(name);
	}

	String getName();

	Collection<OptionCategory> getSubCategories();
	Collection<Option<?>> getOptions();

	void add(Option<?>... options);

	void add(OptionCategory... categories);

	boolean includeInParentTree();

	OptionCategory includeInParentTree(boolean includeInParentTree);
}
