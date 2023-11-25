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

import com.google.common.util.concurrent.AtomicDouble;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import org.lwjgl.nanovg.NanoVG;

public class ResetButtonWidget extends RoundedButtonWidget {

	private final Option<?> option;

	public ResetButtonWidget(int x, int y, int width, int height, Option<?> option) {
		super(x, y, width, height, LiteralText.EMPTY, widget -> {
			option.setDefault();
			Window window = MinecraftClient.getInstance().getWindow();
			int i = window.getScaledWidth();
			int j = window.getScaledHeight();
			Screen current = MinecraftClient.getInstance().currentScreen;
			if (current != null) {
				AtomicDouble scroll = new AtomicDouble();
				current.children().stream()
					.filter(e -> e instanceof RoundedButtonListWidget)
					.map(e -> (RoundedButtonListWidget) e).findFirst().ifPresent(list -> {
						scroll.set(list.getScrollAmount());
					});
				current.init(MinecraftClient.getInstance(), i, j);
				current.children().stream()
					.filter(e -> e instanceof RoundedButtonListWidget)
					.map(e -> (RoundedButtonListWidget) e).findFirst().ifPresent(list -> {
						list.setScrollAmount(scroll.get());
					});
			}
		});
		this.option = option;
	}

	@Override
	public void render(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		this.active = !option.getDefault().equals(option.get());
		super.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		long ctx = NVGHolder.getContext();
		this.active = !option.getDefault().equals(option.get());
		super.renderButton(graphics, mouseX, mouseY, delta);

		Color color = !active ? Colors.GRAY : Colors.WHITE;
		if (active && isHovered()) {
			color = Colors.DARK_YELLOW;
		}

		NanoVG.nvgLineCap(ctx, NanoVG.NVG_ROUND);
		NanoVG.nvgLineJoin(ctx, NanoVG.NVG_ROUND);
		outlineCircle(ctx, getX() + getWidth() / 2, getY() + getHeight() / 2, color, getWidth() / 4, 2, 0, 270);
		NanoVG.nvgBeginPath(ctx);
		NanoVG.nvgMoveTo(ctx, getX() + getWidth() / 2f, getY() + getHeight() / 4f);
		NanoVG.nvgLineTo(ctx, getX() + getWidth() / 2f - 3, getY() + getHeight() / 4f);
		NanoVG.nvgMoveTo(ctx, getX() + getWidth() / 2f, getY() + getHeight() / 2f - 2);
		NanoVG.nvgLineTo(ctx, getX() + getWidth() / 2f - 3, getY() + getHeight() / 4f);
		NanoVG.nvgLineTo(ctx, getX() + getWidth() / 2f, getY() + 2);
		NanoVG.nvgStrokeColor(ctx, color.toNVG());
		NanoVG.nvgStroke(ctx);

	}

	protected MutableText getNarrationMessage() {
		return new TranslatableText("action.reset");
	}

	@Override
	protected Color getWidgetColor() {
		return Colors.TURQUOISE;
	}
}
