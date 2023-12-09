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

public class DoubleOption extends NumberOption<Double> {
	public DoubleOption(String name, Double defaultValue, Double min, Double max) {
		super(name, defaultValue, min, max);
	}

	public DoubleOption(String name, String tooltip, Double defaultValue, Double min, Double max) {
		super(name, tooltip, defaultValue, min, max);
	}

	public DoubleOption(String name, Double defaultValue, ChangeListener<Double> changeListener, Double min, Double max) {
		super(name, defaultValue, changeListener, min, max);
	}

	public DoubleOption(String name, String tooltip, Double defaultValue, ChangeListener<Double> changeListener, Double min, Double max) {
		super(name, tooltip, defaultValue, changeListener, min, max);
	}

	@Override
	public Double clamp(Number value) {
		return value.doubleValue() > getMin() ? (value.doubleValue() < getMax() ? value.doubleValue() : getMax()) : getMin();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = clamp(Double.parseDouble(value));
	}

	@Override
	public String getWidgetIdentifier() {
		return "double";
	}
}
