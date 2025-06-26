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

package io.github.axolotlclient.AxolotlClientConfig.api.options;

import java.util.Collection;
import java.util.Map;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionCategoryImpl;

public interface OptionCategory extends WidgetIdentifieable {

	static OptionCategory create(String name) {
		return new OptionCategoryImpl(name);
	}

	String getName();

	Collection<OptionCategory> getSubCategories();

	Collection<Option<?>> getOptions();

	Map<OptionCategory, Boolean> getSubCategoryMap();

	Map<Option<?>, Boolean> getOptionMap();

	default void add(Option<?>... options) {
		for (Option<?> option : options) {
			add(option, true);
		}
	}

	default void add(OptionCategory... categories) {
		for (OptionCategory category : categories) {
			add(category, category.includeInParentTree());
		}
	}

	OptionCategory add(Option<?> option, boolean save);

	OptionCategory add(OptionCategory category, boolean save);

	@Deprecated(since = "3.0.12")
	boolean includeInParentTree();

	@Deprecated(since = "3.0.12")
	OptionCategory includeInParentTree(boolean includeInParentTree);
}
