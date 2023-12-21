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

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import lombok.Getter;

public abstract class OptionBase<T> implements Option<T> {

	@Getter
	private final String name;

	@Getter
	private final String tooltip;
	private final T defaultValue;
	private final ChangeListener<T> changeListener;
	protected T value;

	public OptionBase(String name, T defaultValue) {
		this(name, defaultValue, val -> {});
	}

	public OptionBase(String name, String tooltip, T defaultValue){
		this(name, tooltip, defaultValue, val -> {});
	}

	public OptionBase(String name, T defaultValue, ChangeListener<T> changeListener){
		this(name, name+".tooltip", defaultValue, changeListener);
	}

	public OptionBase(String name, String tooltip, T defaultValue, ChangeListener<T> changeListener) {
		this.name = name;
		this.tooltip = tooltip;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.changeListener = changeListener;
	}

	public T get() {
		return value;
	}

	@Override
	public T getDefault() {
		return defaultValue;
	}

	public void set(T value) {
		this.value = value;
		changeListener.onChange(value);
	}

	@Override
	public void setDefault() {
		this.value = getDefault();
	}

	public interface ChangeListener<T> {
		void onChange(T newValue);
	}
}
