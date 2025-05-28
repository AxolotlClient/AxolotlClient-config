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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaEntryListWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class VanillaConfigScreen extends Screen implements ConfigScreen {
	private final Screen parent;
	private final ConfigManager configManager;
	private final OptionCategory category;
	private boolean searchVisible;

	public VanillaConfigScreen(Screen parent, OptionCategory category) {
		super(Component.translatable(category.getName()));
		this.parent = parent;
		this.configManager = AxolotlClientConfig.getInstance().getConfigManager(category);
		this.category = category;
	}

	@Override
	protected void init() {
		searchVisible = false;
		EditBox searchInput = addRenderableWidget(new EditBox(font, width/2 - 75, 20, 150, 20, Component.empty()));
		searchInput.visible = false;
		addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, w -> onClose())
			.pos(width / 2 - 75, height - 45).build());
		var list = addRenderableWidget(new VanillaEntryListWidget(configManager, category, width, height, 45, height - 55, 25));
		searchInput.setResponder(list::setSearchFilter);
		addRenderableWidget(new PlainTextButton(width/2 - font.width(getTitle())/2, 25,
			font.width(getTitle()), font.lineHeight, getTitle(), w -> {
			w.visible = false;
			searchInput.visible = searchVisible = true;
			setFocused(searchInput);
			list.setSearchFilter(searchInput.getValue());
		}, font));
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
