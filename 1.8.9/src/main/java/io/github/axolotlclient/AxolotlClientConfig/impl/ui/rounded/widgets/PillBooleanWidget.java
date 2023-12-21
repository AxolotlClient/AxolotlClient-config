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

import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.I18n;

public class PillBooleanWidget extends RoundedButtonWidget implements Updatable {

	protected static final int HANDLE_MARGIN = 3;
	protected static final int OFF_POSITION = HANDLE_MARGIN;
	private final BooleanOption option;
	protected int handleWidth;
	protected int onPosition;
	private boolean state;
	private boolean targetState;
	private double progress;
	private long tickTime = Minecraft.getTime();
	private int notWidth;

	public PillBooleanWidget(int x, int y, int width, int height, BooleanOption option) {
		super(x, y, 40, height, option.get() ? I18n.translate("options.on") : I18n.translate("options.off"), widget -> {
			option.set(!option.get());
			widget.setMessage(option.get() ? I18n.translate("options.on") : I18n.translate("options.off"));
		});

		this.option = option;

		state = targetState = option.get();

		if (state) {
			progress = 1f;
		}

		handleWidth = height - HANDLE_MARGIN * 2;
		onPosition = super.getWidth() - handleWidth - HANDLE_MARGIN;

		this.notWidth = width;
	}

	@Override
	public int getWidth() {
		return notWidth;
	}

	@Override
	public void setWidth(int value) {
		setX(getX() + value - 40);
		this.notWidth = value;
	}

	@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {
		fillRoundedRect(NVGHolder.getContext(), getX(), getY(), super.getWidth(), getHeight(), Colors.foreground(), Math.min(getHeight(), super.getWidth()) / 2f);

		if (((Minecraft.getTime() - tickTime) / 300L) % 2L == 0L) {
			tickTime = Minecraft.getTime();
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

		double x = getX() + OFF_POSITION + (onPosition - OFF_POSITION) * progress;
		double widthProgress = progress > 0.5f ? 1 - progress : progress;
		drawHandle(NVGHolder.getContext(), (float) x, getY(), (float) (handleWidth + (handleWidth * widthProgress)));
	}

	protected void drawHandle(long ctx, float x, float y, float width) {
		fillRoundedRect(ctx, x, y + HANDLE_MARGIN, width, getHeight() - HANDLE_MARGIN * 2,
			getWidgetColor(), Math.min(width, getHeight()) / 2f + HANDLE_MARGIN);

		if (isFocused()) {
			outlineRoundedRect(ctx, x, y + HANDLE_MARGIN, width, getHeight() - HANDLE_MARGIN * 2,
					Colors.text(), Math.min(width, getHeight()) / 2f + HANDLE_MARGIN, 1);
		}
	}

	@Override
	public void onPress() {
		super.onPress();
		state = targetState;
		targetState = !targetState;
		tickTime = Minecraft.getTime();
	}

	public void update() {
		targetState = option.get();
		setMessage(option.get() ? I18n.translate("options.on") : I18n.translate("options.off"));
	}
}
