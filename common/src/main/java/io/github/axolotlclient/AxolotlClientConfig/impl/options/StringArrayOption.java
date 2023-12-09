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

import java.util.Arrays;

import lombok.Getter;

@Getter
public class StringArrayOption extends OptionBase<String> {

	private final String[] values;

	public StringArrayOption(String name, String... values) {
		super(name, values[0]);
		this.values = values;
	}

	public StringArrayOption(String name, String[] values, String defaultValue) {
		super(name, defaultValue);
		this.values = values;
	}

	public StringArrayOption(String name, String[] values, String defaultValue, ChangeListener<String> changeListener) {
		super(name, defaultValue, changeListener);
		this.values = values;
	}

	public StringArrayOption(String name, String[] values, String tooltip, String defaultValue) {
		super(name, tooltip, defaultValue);
		this.values = values;
	}

	public StringArrayOption(String name, String[] values, String tooltip, String defaultValue, ChangeListener<String> changeListener) {
		super(name, tooltip, defaultValue, changeListener);
		this.values = values;
	}

	@Override
	public String toSerializedValue() {
		return get();
	}

	@Override
	public void fromSerializedValue(String value) {
		if (Arrays.asList(values).contains(value)) {
			this.value = value;
		} else {
			setDefault();
		}
	}

	@Override
	public String getWidgetIdentifier() {
		return "string[]";
	}

	@Override
	public void set(String value) {
		if (Arrays.asList(values).contains(value)) {
			super.set(value);
		}
	}
}
