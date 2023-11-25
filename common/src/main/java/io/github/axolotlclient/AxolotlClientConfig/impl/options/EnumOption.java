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

import lombok.Getter;

public class EnumOption<T extends Enum<T>> extends OptionBase<T> {

	@Getter
	private final Class<T> clazz;

	public EnumOption(String name, Class<T> e) {
		this(name, e, e.getEnumConstants()[0]);
	}

	public EnumOption(String name, Class<T> e, T defaultValue) {
		super(name, defaultValue);
		clazz = e;
	}

	public EnumOption(String name, Class<T> e, T defaultValue, ChangeListener<T> changeListener) {
		super(name, defaultValue, changeListener);
		this.clazz = e;
	}

	@Override
	public String toSerializedValue() {
		return get().toString();
	}

	@Override
	public void fromSerializedValue(String value) {
		for (T val : clazz.getEnumConstants()) {
			if (val.toString().equals(value)) {
				this.value = val;
			}
		}
	}

	@Override
	public String getWidgetIdentifier() {
		return "enum";
	}


}
