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

public class IntegerOption extends NumberOption<Integer> {
	public IntegerOption(String name, Integer defaultValue, Integer min, Integer max) {
		super(name, defaultValue, min, max);
	}

	public IntegerOption(String name, String tooltip, Integer defaultValue, Integer min, Integer max) {
		super(name, tooltip, defaultValue, min, max);
	}

	public IntegerOption(String name, Integer defaultValue, ChangeListener<Integer> changeListener, Integer min, Integer max) {
		super(name, defaultValue, changeListener, min, max);
	}

	public IntegerOption(String name, String tooltip, Integer defaultValue, ChangeListener<Integer> changeListener, Integer min, Integer max) {
		super(name, tooltip, defaultValue, changeListener, min, max);
	}

	@Override
	protected Integer clamp(Number value) {
		return value.intValue() > getMin() ? (value.intValue() < getMax() ? value.intValue() : getMax()) : getMin();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = clamp(Integer.parseInt(value));
	}

	@Override
	public String getWidgetIdentifier() {
		return "integer";
	}
}
