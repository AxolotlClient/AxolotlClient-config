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

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.NumberOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.nanovg.NanoVG;

public class SliderWidget<O extends NumberOption<N>, N extends Number> extends net.minecraft.client.gui.components.AbstractSliderButton implements DrawingUtil, Updatable {
	private static final DecimalFormat format = new DecimalFormat("0.##");
	private final O option;
	private boolean dragging;

	public SliderWidget(int x, int y, int width, int height, O option) {
		super(x, y, width, height, Component.literal(format.format(option.get().doubleValue())), 0);
		this.value = ((option.get().doubleValue() - option.getMin().doubleValue()) / (option.getMax().doubleValue() - option.getMin().doubleValue()));
		this.option = option;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		long ctx = NVGHolder.getContext();
		double val = ((option.get().doubleValue() - option.getMin().doubleValue()) / (option.getMax().doubleValue() - option.getMin().doubleValue()));
		if (!isHoveredOrFocused() &&
			val != value) {
			value = val;
			updateMessage();
		}

		fillRoundedRect(ctx, getX(), getY() + getHeight() / 2f - 1, getWidth(), 2, Colors.foreground(), 1);

		NanoVG.nvgBeginPath(ctx);
		NanoVG.nvgCircle(ctx, (float) (getX() + (this.value * (getWidth() - 4))), getY() + getHeight() / 2f, 4);
		NanoVG.nvgFillColor(ctx, isHovered() ? Colors.accent2().toNVG() : Colors.accent().toNVG());
		NanoVG.nvgFill(ctx);

		if (isFocused()) {
			NanoVG.nvgBeginPath(ctx);
			NanoVG.nvgCircle(ctx, (float) (getX() + (this.value * (getWidth() - 4))), getY() + getHeight() / 2f, 4);
			NanoVG.nvgStrokeColor(ctx, Colors.highlight().toNVG());
			NanoVG.nvgStroke(ctx);
		}

		drawCenteredString(ctx, NVGHolder.getFont(), this.getMessage().getString(), (float) (getX() + (this.value * (getWidth() - 4))),
			this.getY() + (this.getHeight() / 2f - 8) / 2f - 4, Colors.text());
		if (this.isHovered()) {
			graphics.requestCursor(this.dragging ? CursorTypes.RESIZE_EW : CursorTypes.POINTING_HAND);
		}
	}

	public void updateMessage() {
		setMessage(Component.literal(format.format(option.get().doubleValue())));
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

	@Override
	public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
		this.dragging = this.active;
		super.onClick(mouseButtonEvent, bl);
	}

	@Override
	public void onRelease(MouseButtonEvent mouseButtonEvent) {
		this.dragging = false;
		super.onRelease(mouseButtonEvent);
	}
}
