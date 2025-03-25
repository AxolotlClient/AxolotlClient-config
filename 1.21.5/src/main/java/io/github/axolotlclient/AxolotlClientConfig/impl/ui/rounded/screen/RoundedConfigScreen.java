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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.TextFieldWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.TextOnlyButtonWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class RoundedConfigScreen extends Screen implements ConfigScreen, DrawingUtil {

	private final Screen parent;
	private final ConfigManager configManager;
	private final OptionCategory category;
	private boolean searchVisible;

	public RoundedConfigScreen(Screen parent, OptionCategory category) {
		super(Component.translatable(category.getName()));
		this.parent = parent;
		this.configManager = AxolotlClientConfig.getInstance().getConfigManager(category);
		this.category = category;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		NVGUtil.wrap(ctx -> {
			NVGHolder.setContext(ctx);
			super.render(graphics, mouseX, mouseY, delta);
		});
	}

	@Override
	public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.renderBackground(graphics, mouseX, mouseY, delta);

		graphics.fill(0, 0, 1, 1, 0); // Don't ask, it seems to work

		fillRoundedRect(NVGHolder.getContext(), 15, 15, width - 30, height - 30, Colors.background(), 12);
	}

	@Override
	protected void init() {
		searchVisible = false;
		TextFieldWidget searchInput = addRenderableWidget(new TextFieldWidget(width/2 - 75, 20, 150, 20, Component.empty()));
		searchInput.visible = false;
		addRenderableWidget(new RoundedButtonWidget(width / 2 - 75, height - 40,
			CommonComponents.GUI_BACK, w -> onClose()));
		RoundedButtonListWidget list = addRenderableWidget(new RoundedButtonListWidget(configManager, category, width, height, 45, height - 55, 25));
		searchInput.setChangedListener(list::setSearchFilter);
		addRenderableWidget(TextOnlyButtonWidget.centeredWidget(width/2, 25, getTitle(), w -> {
			w.visible = false;
			searchInput.visible = searchVisible = true;
			setFocused(searchInput);
			list.setSearchFilter(searchInput.getText());
		}));
	}

	@Override
	public void onClose() {
		if (searchVisible) {
			rebuildWidgets();
		} else {
			minecraft.setScreen(parent);
		}
	}

	@Override
	public void removed() {
		if (configManager != null) {
			configManager.save();
		}
	}
}
