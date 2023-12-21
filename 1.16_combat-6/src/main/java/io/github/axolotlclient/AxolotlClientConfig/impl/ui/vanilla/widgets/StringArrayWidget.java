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

import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringArrayOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class StringArrayWidget extends VanillaButtonWidget {

	private final StringArrayOption option;

	public StringArrayWidget(int x, int y, int width, int height, StringArrayOption option) {
		super(x, y, width, height, new TranslatableText(option.get()), widget -> {
		});
		this.option = option;
	}

	@Override
	public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		if (!getMessage().getString().equals(new TranslatableText(option.get()).getString())) {
			setMessage(new TranslatableText(option.get()));
		}
		super.renderButton(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		String[] values = option.getValues();
		int i = 0;
		while (!values[i].equals(option.get())) {
			i += 1;
		}
		i += 1;
		if (i >= values.length) {
			i = 0;
		}
		option.set(values[i]);
		setMessage(new TranslatableText(option.get()));
	}
}
