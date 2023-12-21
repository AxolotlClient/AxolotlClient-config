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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import java.text.DecimalFormat;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.NumberOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.lwjgl.nanovg.NanoVG;

public class SliderWidget<O extends NumberOption<N>, N extends Number> extends net.minecraft.client.gui.widget.SliderWidget implements DrawingUtil, Updatable {
	private final O option;

	public SliderWidget(int x, int y, int width, int height, O option) {
		super(x, y, width, height, new TranslatableText(String.valueOf(option.get())), 0);
		this.value = ((option.get().doubleValue() - option.getMin().doubleValue()) / (option.getMax().doubleValue() - option.getMin().doubleValue()));
		this.option = option;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		long ctx = NVGHolder.getContext();
		double val = ((option.get().doubleValue() - option.getMin().doubleValue()) / (option.getMax().doubleValue() - option.getMin().doubleValue()));
		if (!(isHovered() || isFocused()) &&
			val != value) {
			value = val;
			updateMessage();
		}

		fillRoundedRect(ctx, getX(), getY() + getHeight() / 2f - 1, getWidth(), 2, Colors.foreground(), 1);

		NanoVG.nvgBeginPath(ctx);
		NanoVG.nvgCircle(ctx, (float) (getX() + (this.value * (getWidth() - 4))), getY() + getHeight() / 2f, 4);
		NanoVG.nvgFillColor(ctx, hovered ? Colors.accent2().toNVG() : Colors.accent().toNVG());
		NanoVG.nvgFill(ctx);

		if (isFocused()) {
			NanoVG.nvgBeginPath(ctx);
			NanoVG.nvgCircle(ctx, (float) (getX() + (this.value * (getWidth() - 4))), getY() + getHeight() / 2f, 4);
			NanoVG.nvgStrokeColor(ctx, Colors.highlight().toNVG());
			NanoVG.nvgStroke(ctx);
		}

		drawCenteredString(ctx, NVGHolder.getFont(), this.getMessage().getString(), (float) (getX() + (this.value * (getWidth() - 4))),
			this.getY() + (this.getHeight() / 2f - 8) / 2f - 4, Colors.text());
	}

	public void updateMessage() {
		DecimalFormat format = new DecimalFormat("0.##");
		setMessage(new LiteralText(format.format(option.get().doubleValue())));
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
