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

import org.jetbrains.annotations.NotNull;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.RecreatableScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.TextFieldWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.PlainTextButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

public class VanillaConfigScreen extends io.github.axolotlclient.AxolotlClientConfig.impl.ui.Screen implements ConfigScreen, RecreatableScreen {
	private final Screen parent;
	private final ConfigManager configManager;
	private final OptionCategory category;
	private boolean searchVisible;

	public VanillaConfigScreen(Screen parent, OptionCategory category) {
		super(I18n.translate(category.getName()));
		this.parent = parent;
		this.configManager = AxolotlClientConfig.getInstance().getConfigManager(category);
		this.category = category;
	}

	@Override
	public void init() {
		searchVisible = false;
		TextFieldWidget searchInput = addDrawableChild(new TextFieldWidget(textRenderer, width/2 - 75, 20, 150, 20, ""));
		searchInput.setVisible(false);
		addDrawableChild(new VanillaButtonWidget(width / 2 - 75, height - 45, 150, 20,
			I18n.translate("gui.back"), w -> {
			if (searchVisible) {
				clearAndInit();
			} else {
				Minecraft.getInstance().openScreen(parent);
			}
		}));
		var list = addDrawableChild(new VanillaButtonListWidget(configManager, category, width, height, 45, height - 55, 25));
		searchInput.setChangedListener(list::setSearchFilter);
		addDrawableChild(new PlainTextButtonWidget(width/2 - textRenderer.getWidth(getTitle())/2, 25,
			textRenderer.getWidth(getTitle()), textRenderer.fontHeight, getTitle(), w -> {
			w.visible = false;
			searchInput.visible = searchVisible = true;
			setFocusedChild(searchInput);
			searchInput.setFocused(true);
			list.setSearchFilter(searchInput.getText());
		}, textRenderer));
	}

	@Override
	public void removed() {
		if (configManager != null) {
			configManager.save();
		}
	}

	@Override
	public @NotNull Screen recreate() {
		return ConfigStyles.createScreen(RecreatableScreen.tryRecreate(parent), category);
	}
}
