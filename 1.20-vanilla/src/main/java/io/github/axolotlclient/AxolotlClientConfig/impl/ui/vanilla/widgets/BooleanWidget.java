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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.CommonTexts;

public class BooleanWidget extends ButtonWidget implements Updatable {

	private final BooleanOption option;

	public BooleanWidget(int x, int y, int width, int height, BooleanOption option) {
		super(x, y, width, height, option.get() ? CommonTexts.ON : CommonTexts.OFF, widget -> {
			option.set(!option.get());
			widget.setMessage(option.get() ? CommonTexts.ON : CommonTexts.OFF);
		}, DEFAULT_NARRATION);
		this.option = option;
	}

	public void update() {
		setMessage(option.get() ? CommonTexts.ON : CommonTexts.OFF);
	}
}
