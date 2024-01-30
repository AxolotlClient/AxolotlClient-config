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
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.system.SoundManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.nanovg.NanoVG;

public class SliderWidget<O extends NumberOption<N>, N extends Number> extends RoundedButtonWidget {

	private final O option;
	protected double value;

	public SliderWidget(int x, int y, int width, int height, O option) {
		super(x, y, width, height, String.valueOf(option.get()), widget -> {
		});
		this.value = ((option.get().doubleValue() - option.getMin().doubleValue()) / (option.getMax().doubleValue() - option.getMin().doubleValue()));
		this.option = option;
	}

	@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {
		long ctx = NVGHolder.getContext();
		double val = ((option.get().doubleValue() - option.getMin().doubleValue()) / (option.getMax().doubleValue() - option.getMin().doubleValue()));
		if (val != value) {
			value = val;
			updateMessage();
		}

		fillRoundedRect(ctx, getX(), getY() + getHeight() / 2f - 1, getWidth(), 2, Colors.foreground(), 1);

		NanoVG.nvgBeginPath(ctx);
		NanoVG.nvgCircle(ctx, (float) (getX() + (this.value * (getWidth() - 4))), getY() + getHeight() / 2f, 4);
		NanoVG.nvgFillColor(ctx, isHovered() ? Colors.accent2().toNVG() : Colors.accent().toNVG());
		NanoVG.nvgFill(ctx);

		drawCenteredString(ctx, NVGHolder.getFont(), this.getMessage(), (float) (getX() + (this.value * (getWidth() - 4))),
			this.getY() + (this.getHeight() / 2f - 8) / 2f - 4, Colors.text());
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		this.setValueFromMouse(mouseX);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean bl = keyCode == 263;
		if (bl || keyCode == 262) {
			float f = bl ? -1.0F : 1.0F;
			this.setValue(this.value + (double) (f / (float) (this.getWidth() - 8)));
		}

		return false;
	}

	private void setValueFromMouse(double mouseX) {
		this.setValue((mouseX - (double) (this.getX() + 4)) / (double) (this.getWidth() - 8));
	}

	@SuppressWarnings("unchecked")
	private void setValue(double value) {
		double d = this.value;
		this.value = MathHelper.clamp(value, 0.0, 1.0);
		if (d != this.value) {
			option.set((N) (Double) (option.getMin().doubleValue() +
				(value * (option.getMax().doubleValue() - option.getMin().doubleValue()))));
		}

		this.updateMessage();
	}

	private void updateMessage() {
		DecimalFormat format = new DecimalFormat("0.##");
		setMessage(format.format(option.get().doubleValue()));
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		this.setValueFromMouse(mouseX);
		super.onDrag(mouseX, mouseY, deltaX, deltaY);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		super.playDownSound(Minecraft.getInstance().getSoundManager());
	}
}
