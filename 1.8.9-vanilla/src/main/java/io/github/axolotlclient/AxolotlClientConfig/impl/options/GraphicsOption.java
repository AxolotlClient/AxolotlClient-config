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

import java.util.Base64;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Graphics;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.GraphicsImpl;

public class GraphicsOption extends OptionBase<Graphics> {
	public GraphicsOption(String name, int width, int height) {
		super(name, new GraphicsImpl(width, height));
	}

	public GraphicsOption(String name, int width, int height, ChangeListener<Graphics> changeListener) {
		super(name, new GraphicsImpl(width, height), changeListener);
	}

	public GraphicsOption(String name, int[][] data) {
		super(name, new GraphicsImpl(data));
	}

	public GraphicsOption(String name, int[][] data, ChangeListener<Graphics> changeListener) {
		super(name, new GraphicsImpl(data), changeListener);
	}

	public GraphicsOption(String name, Graphics data) {
		super(name, data);
	}

	public GraphicsOption(String name, Graphics data, ChangeListener<Graphics> changeListener) {
		super(name, data, changeListener);
	}

	@Override
	public String toSerializedValue() {
		return Base64.getEncoder().encodeToString(get().getPixelData());
	}

	@Override
	public void fromSerializedValue(String value) {
		byte[] data = Base64.getDecoder().decode(value);
		this.value.setPixelData(data);
	}

	@Override
	public String getWidgetIdentifier() {
		return "graphics";
	}
}
