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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
public class OptionCategoryImpl implements OptionCategory {

	private final String name;

	private final Collection<Option<?>> options = new ArrayList<>();
	private final Collection<OptionCategory> subCategories = new ArrayList<>();

	@Accessors(fluent = true)
	@Setter
	private boolean includeInParentTree = true;

	@Override
	public void add(Option<?>... options) {
		Collections.addAll(this.options, options);
	}

	@Override
	public void add(OptionCategory... categories) {
		Collections.addAll(subCategories, categories);
	}


	@Override
	public String getWidgetIdentifier() {
		return "category";
	}
}
