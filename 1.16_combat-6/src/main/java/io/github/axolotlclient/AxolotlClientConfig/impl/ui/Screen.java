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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.util.List;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public abstract class Screen extends net.minecraft.client.gui.screen.Screen {

	protected final String title;
	private final List<Drawable> drawables = Lists.newArrayList();
	protected MinecraftClient client;

	public Screen(String title) {
		super(new TranslatableText(title));
		this.title = title;
	}

	@Override
	public void init() {
		client = MinecraftClient.getInstance();
		drawables.clear();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		drawables.forEach(drawable -> drawable.render(matrices, mouseX, mouseY, delta));
	}

	protected <T extends Element & net.minecraft.client.gui.Drawable> T addDrawableChild(T drawableElement) {
		this.drawables.add(drawableElement);
		return this.addChild(drawableElement);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		drawables.clear();
		super.resize(client, width, height);
	}
}
