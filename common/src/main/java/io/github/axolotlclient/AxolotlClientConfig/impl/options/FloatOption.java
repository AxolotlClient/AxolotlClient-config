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

public class FloatOption extends NumberOption<Float> {
	public FloatOption(String name, Float defaultValue, Float min, Float max) {
		super(name, defaultValue, min, max);
	}

	public FloatOption(String name, String tooltip, Float defaultValue, Float min, Float max) {
		super(name, tooltip, defaultValue, min, max);
	}

	public FloatOption(String name, Float defaultValue, ChangeListener<Float> changeListener, Float min, Float max) {
		super(name, defaultValue, changeListener, min, max);
	}

	public FloatOption(String name, String tooltip, Float defaultValue, ChangeListener<Float> changeListener, Float min, Float max) {
		super(name, tooltip, defaultValue, changeListener, min, max);
	}

	@Override
	protected Float clamp(Number value) {
		return value.floatValue() > getMin() ? (value.floatValue() < getMax() ? value.floatValue() : getMax()) : getMin();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = clamp(Float.parseFloat(value));
	}

	@Override
	public String getWidgetIdentifier() {
		return "float";
	}
}
