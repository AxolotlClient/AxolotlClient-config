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
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonWidget;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;

public class RoundedConfigScreen extends Screen implements ConfigScreen, DrawingUtil {

	private final Screen parent;
	@Getter
	private final ConfigManager configManager;
	private final OptionCategory category;

	public RoundedConfigScreen(Screen parent, OptionCategory category) {
		super(Text.translatable(category.getName()));
		this.parent = parent;
		this.configManager = AxolotlClientConfig.getInstance().getConfigManager(category);
		this.category = category;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		NVGMC.wrap(ctx -> {
			fillRoundedRect(ctx, 15, 15, width - 30, height - 30, Colors.background(), 12);
			drawCenteredString(ctx, NVGHolder.getFont(), getTitle().getString(), width / 2f, 25, Colors.text());
			NVGHolder.setContext(ctx);
			super.render(graphics, mouseX, mouseY, delta);
		});
	}

	@Override
	public void init() {
		addDrawableSelectableElement(new RoundedButtonWidget(width / 2 - 75, height - 40,
			CommonTexts.BACK, w -> closeScreen()));
		addDrawableSelectableElement(new RoundedButtonListWidget(configManager, category, width, height, 45, height - 55, 25));
	}

	@Override
	public void closeScreen() {
		client.setScreen(parent);
	}

	@Override
	public void removed() {
		configManager.save();
	}
}
