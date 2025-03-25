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

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.ButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class RoundedButtonListWidget extends ButtonListWidget {
	public RoundedButtonListWidget(ConfigManager manager, OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(manager, category, screenWidth, screenHeight, top, bottom, entryHeight);
	}

	@Override
	protected void addOptions(ConfigManager manager, Collection<Option<?>> options) {
		if (manager != null) {
			options.stream()
				.filter(o -> !manager.getSuppressedNames().contains(o.getName()))
				.forEach(o -> addEntry(o, null));
		} else {
			options.forEach(o -> addEntry(o, null));
		}
	}

	@Override
	public void addEntry(Option<?> first, @Nullable Option<?> second) {
		addEntry(createOptionEntry(createWidget(width / 2 + WIDGET_ROW_RIGHT, first), first, null, null));
	}

	@Override
	protected Entry createOptionEntry(AbstractWidget widget, Option<?> option, @Nullable AbstractWidget other, @Nullable Option<?> otherOption) {
		return new RoundedOptionEntry(widget, option);
	}

	@Override
	protected void renderDecorations(GuiGraphics graphics, int mouseX, int mouseY) {
		super.renderDecorations(graphics, mouseX, mouseY);
		if (getHovered() != null && getHovered() instanceof RoundedOptionEntry) {
			DrawUtil.drawTooltip(NVGHolder.getContext(), NVGHolder.getFont(), ((RoundedOptionEntry) getHovered()).option,
				mouseX, mouseY);
		}
	}

	@Override
	protected void enableScissor(GuiGraphics guiGraphics) {

	}

	private class RoundedOptionEntry extends Entry implements DrawingUtil {

		private final Option<?> option;

		public RoundedOptionEntry(AbstractWidget widget, Option<?> option) {
			super(ImmutableList.of(widget,
				new ResetButtonWidget<>(widget.getX() + widget.getWidth() - 20, 0, 20, widget.getHeight(), option)));
			widget.setWidth(widget.getWidth() - 22);
			this.option = option;
		}

		@Override
		public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			drawScrollingText(NVGHolder.getContext(), NVGHolder.getFont(), Component.translatable(option.getName()).getString(),
				width / 2 + WIDGET_ROW_LEFT, y, WIDGET_WIDTH, entryHeight, Colors.accent());
		}
	}
}
