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

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;

public class ColorOption extends OptionBase<Color> {

	public ColorOption(String name, Color defaultValue) {
		super(name, defaultValue.mutable());
	}

	public ColorOption(String name, String tooltip, Color defaultValue) {
		super(name, tooltip, defaultValue);
	}

	public ColorOption(String name, Color defaultValue, ChangeListener<Color> changeListener) {
		super(name, defaultValue.mutable(), changeListener);
	}

	public ColorOption(String name, String tooltip, Color defaultValue, ChangeListener<Color> changeListener) {
		super(name, tooltip, defaultValue, changeListener);
	}

	@Override
	public Color get() {
		return super.get().get();
	}

	public Color getOriginal(){
		return super.get();
	}

	@Override
	public String toSerializedValue() {
		return get().toString();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = Color.parse(value);
	}

	@Override
	public String getWidgetIdentifier() {
		return "color";
	}


}
