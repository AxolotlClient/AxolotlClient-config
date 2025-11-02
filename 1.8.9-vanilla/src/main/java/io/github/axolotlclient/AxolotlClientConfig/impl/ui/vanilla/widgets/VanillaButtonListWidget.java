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

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ClickableWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.ButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

public class VanillaButtonListWidget extends ButtonListWidget {
	public VanillaButtonListWidget(ConfigManager manager, OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(manager, category, screenWidth, screenHeight, top, bottom, entryHeight);
		setRenderBackground(false);
		setRenderHeader(false, headerHeight);
		setRenderHorizontalShadows(false);
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
	protected Entry createOptionEntry(ClickableWidget widget, Option<?> option, @Nullable ClickableWidget other, @Nullable Option<?> otherOption) {
		return new VanillaOptionEntry(widget, option);
	}

	@Override
	protected void renderDecorations(int mouseX, int mouseY) {
		if (getHoveredEntry() != null && getHoveredEntry() instanceof VanillaOptionEntry){
			DrawUtil.drawTooltip(((VanillaOptionEntry) getHoveredEntry()).option, mouseX, mouseY);
		}
	}

	private class VanillaOptionEntry extends Entry {

		private final Option<?> option;

		public VanillaOptionEntry(ClickableWidget widget, Option<?> option) {
			super(ImmutableList.of(widget,
				new ResetButtonWidget<>(widget.getX() + widget.getWidth() - 40, 0, 40, widget.getHeight(), option)));
			widget.setWidth(widget.getWidth() - 42);
			this.option = option;
		}

		@Override
		public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			DrawUtil.drawScrollingText(I18n.translate(option.getName()), width / 2 + WIDGET_ROW_LEFT,
				y, WIDGET_WIDTH, entryHeight, Colors.text());
		}
	}
}
