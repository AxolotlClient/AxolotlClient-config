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

import java.text.DecimalFormat;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.NumberOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import net.minecraft.text.Text;

public class SliderWidget<O extends NumberOption<N>, N extends Number> extends net.minecraft.client.gui.widget.SliderWidget implements Updatable {
	private final O option;

	public SliderWidget(int x, int y, int width, int height, O option) {
		super(x, y, width, height, Text.literal(String.valueOf(option.get())), 0);
		this.value = ((option.get().doubleValue() - option.getMin().doubleValue()) / (option.getMax().doubleValue() - option.getMin().doubleValue()));
		this.option = option;
	}

	public void updateMessage() {
		DecimalFormat format = new DecimalFormat("0.##");
		setMessage(Text.literal(format.format(option.get().doubleValue())));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void applyValue() {
		option.set((N) (Double) (option.getMin().doubleValue() +
			(value * (option.getMax().doubleValue() - option.getMin().doubleValue()))));
	}

	public void update() {
		this.value = ((option.get().doubleValue() - option.getMin().doubleValue()) / (option.getMax().doubleValue() - option.getMin().doubleValue()));
		updateMessage();
	}
}
