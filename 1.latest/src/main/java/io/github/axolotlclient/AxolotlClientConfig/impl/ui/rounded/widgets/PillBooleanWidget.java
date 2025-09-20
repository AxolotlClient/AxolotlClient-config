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

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.CommonComponents;

@SuppressWarnings("unused")
public class PillBooleanWidget extends RoundedButtonWidget implements Updatable {

	protected static final int HANDLE_MARGIN = 3;
	protected static final int OFF_POSITION = HANDLE_MARGIN;
	private final BooleanOption option;
	protected int handleWidth;
	private boolean state;
	private boolean targetState;
	private double progress;
	private long tickTime = Util.getMillis();

	public PillBooleanWidget(int x, int y, int width, int height, BooleanOption option) {
		super(x + width - 40 - 22, y, 40 + 22, height, option.get() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF, widget -> {
			option.set(!option.get());
			widget.setMessage(option.get() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
		});

		this.option = option;

		state = targetState = option.get();

		if (state) {
			progress = 1f;
		}

		handleWidth = height - HANDLE_MARGIN * 2;
	}

	@Override
	public void setWidth(int value) {
		setX(getX() + getWidth() - value - 22);
		super.setWidth(value);
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		fillRoundedRect(NVGHolder.getContext(), getX(), getY(), getWidth(), getHeight(), Colors.foreground(), Math.min(getHeight(), getWidth()) / 2f);

		if (((Util.getMillis() - tickTime) / 300L) % 2L == 0L) {
			tickTime = Util.getMillis();
			if (state != targetState) {
				if (targetState) {
					progress = Math.min(1, progress + 0.05f);
				} else {
					progress = Math.max(0, progress - 0.05f);
				}
				if (progress >= 1 || progress <= 0) {
					state = targetState;
				}
			}
		}

		double x = getX() + OFF_POSITION + (getWidth() - handleWidth - HANDLE_MARGIN - OFF_POSITION) * progress;
		double widthProgress = progress > 0.5f ? 1 - progress : progress;
		drawHandle(NVGHolder.getContext(), (float) x, getY(), (float) (handleWidth + (handleWidth * widthProgress)));
		if (this.isHovered()) {
			graphics.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
		}
	}

	protected void drawHandle(long ctx, float x, float y, float width) {
		fillRoundedRect(ctx, x, y + HANDLE_MARGIN, width, getHeight() - HANDLE_MARGIN * 2,
			getWidgetColor(), Math.min(width, getHeight()) / 2f + HANDLE_MARGIN);

		if (isFocused()) {
			outlineRoundedRect(ctx, x, y + HANDLE_MARGIN, width, getHeight() - HANDLE_MARGIN * 2,
				Colors.highlight(), Math.min(width, getHeight()) / 2f + HANDLE_MARGIN, 1);
		}
	}

	@Override
	public void onPress(InputWithModifiers modifiers) {
		super.onPress(modifiers);
		state = targetState;
		targetState = !targetState;
		tickTime = Util.getMillis();
	}

	public void update() {
		targetState = option.get();
		setMessage(option.get() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
	}
}
