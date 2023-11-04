package io.github.axolotlclient.AxolotlClientConfig.impl.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public class OptionCategoryImpl implements OptionCategory {

	@Getter
	private final String name;

	@Getter
	private final Collection<Option<?>> options = new ArrayList<>();
	@Getter
	private final Collection<OptionCategory> subCategories = new ArrayList<>();

	@Accessors(fluent = true)
	@Setter @Getter
	private boolean includeInParentTree = true;

	@Override
	public void add(Option<?>... options){
		Collections.addAll(this.options, options);
	}

	@Override
	public void add(OptionCategory... categories){
		Collections.addAll(subCategories, categories);
	}


	@Override
	public String getWidgetIdentifier() {
		return "category";
	}
}
