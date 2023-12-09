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

public class BooleanOption extends OptionBase<Boolean> {

	public BooleanOption(String name, Boolean defaultValue) {
		super(name, defaultValue);
	}

	public BooleanOption(String name, String tooltip, Boolean defaultValue) {
		super(name, tooltip, defaultValue);
	}

	public BooleanOption(String name, Boolean defaultValue, ChangeListener<Boolean> changeListener) {
		super(name, defaultValue, changeListener);
	}

	public BooleanOption(String name, String tooltip, Boolean defaultValue, ChangeListener<Boolean> changeListener) {
		super(name, tooltip, defaultValue, changeListener);
	}

	public void toggle() {
		set(!get());
	}

	@Override
	public String toSerializedValue() {
		return String.valueOf(get());
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = Boolean.valueOf(value);
	}

	@Override
	public String getWidgetIdentifier() {
		return "boolean";
	}
}
