/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.AxolotlClientConfig.impl.options;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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

	private final Map<Option<?>, Boolean> options = new LinkedHashMap<>();
	private final Map<OptionCategory, Boolean> subCategories = new LinkedHashMap<>();

	@Override
	public Collection<OptionCategory> getSubCategories() {
		return subCategories.keySet();
	}

	@Override
	public Collection<Option<?>> getOptions() {
		return options.keySet();
	}

	@Override
	public Map<OptionCategory, Boolean> getSubCategoryMap() {
		return subCategories;
	}

	@Override
	public Map<Option<?>, Boolean> getOptionMap() {
		return options;
	}

	@Accessors(fluent = true)
	@Setter
	@Getter
	private boolean includeInParentTree = true;

	public OptionCategory add(Option<?> option, boolean save) {
		this.options.put(option, save);
		return this;
	}

	public OptionCategory add(OptionCategory category, boolean save) {
		this.subCategories.put(category, save);
		return this;
	}

	@Override
	public String getWidgetIdentifier() {
		return "category";
	}
}
