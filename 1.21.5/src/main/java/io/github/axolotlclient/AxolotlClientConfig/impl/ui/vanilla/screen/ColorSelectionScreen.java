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

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.FloatOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ColorSelectionScreen extends Screen {
	private final ColorOption option;
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("axolotlclientconfig", "textures/gui/colorwheel.png");
	private final Screen parent;
	private final BooleanOption chroma;
	private final FloatOption speed;
	private final IntegerOption alpha;
	private int selectorRadius;
	private float selectorX;
	private float selectorY;
	private int buttonsX;

	public ColorSelectionScreen(Screen parent, ColorOption option) {
		super(Component.translatable("select_color"));
		this.option = option;
		this.parent = parent;
		chroma = new BooleanOption("option.chroma", option.getOriginal().isChroma(), v -> option.getOriginal().setChroma(v));
		speed = new FloatOption("option.speed", option.getOriginal().getChromaSpeed(), v -> option.getOriginal().setChromaSpeed(v), 0f, 4f);
		alpha = new IntegerOption("option.alpha", option.getOriginal().getAlpha(), val -> {
			option.getOriginal().setAlpha(val);
			children().forEach(e -> {
				if (e instanceof EditBox) {
					((EditBox) e).setValue(option.getOriginal().toString().split(";")[0]);
				}
			});
		}, 0, 255);
	}

	@Override
	public void init() {
		super.init();
		addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, buttonWidget -> onClose())
			.pos(width / 2 - 75, height - 40).build());

		selectorRadius = Math.max(Math.min(width / 4 - 10, (height) / 2 - 60), 75);
		selectorX = width / 4f - selectorRadius;
		selectorY = height / 2f - selectorRadius;

		buttonsX = (int) Math.max(width / 2f + 25, selectorX + selectorRadius * 2 + 10);

		int y = 120;
		addRenderableWidget(ConfigStyles.createWidget(buttonsX, y, 150, 20, chroma));
		y += 45;
		if (height > 300) {
			addRenderableWidget(ConfigStyles.createWidget(buttonsX, y, 150, 20, speed));
			y += 45;
		}
		addRenderableWidget(ConfigStyles.createWidget(buttonsX, y, 150, 20, alpha));
		y += 45;
		if (this.height - 250 > 0) {
			y -= 20;
			EditBox text = new EditBox(minecraft.font, buttonsX, y, 150, 20, Component.empty());
			text.setResponder(s -> {
				try {
					option.set(Color.parse(s));
					option.getOriginal().setChroma(chroma.get());
					option.getOriginal().setChromaSpeed(speed.get());

					children().forEach(e -> {
						if (e instanceof Updatable) {
							((Updatable) e).update();
						}
					});
				} catch (Throwable ignored) {
				}
			});
			text.setValue(option.get().toString().split(";")[0]);
			addRenderableWidget(text);
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredString(minecraft.font, title, width / 2, 20, Colors.text().toInt());

		graphics.blit(RenderType::guiTextured, texture, (int) selectorX, (int) selectorY, 0, 0, selectorRadius * 2, selectorRadius * 2, selectorRadius * 2, selectorRadius * 2);

		DrawUtil.outlineRect(graphics, (int) selectorX, (int) selectorY, selectorRadius * 2, selectorRadius * 2, Colors.BLACK.toInt());

		graphics.drawString(minecraft.font, Component.translatable("option.current"), buttonsX, 40, Colors.text().toInt());

		DrawUtil.fillRect(graphics, buttonsX, 55, 150, 40, option.get().toInt());
		DrawUtil.outlineRect(graphics, buttonsX, 55, 150, 40, Colors.BLACK.toInt());

		int y = 105;
		graphics.drawString(minecraft.font, Component.translatable("option.chroma"), buttonsX, y, Colors.text().toInt());
		y += 45;
		if (height > 300) {
			graphics.drawString(minecraft.font, Component.translatable("option.speed"), buttonsX, y, Colors.text().toInt());
			y += 45;
		}
		graphics.drawString(minecraft.font, Component.translatable("option.alpha"), buttonsX, y, Colors.text().toInt());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (button == 0) {
			if (mouseX >= selectorX && mouseX <= selectorX + selectorRadius * 2 &&
				mouseY >= selectorY && mouseY <= selectorY + selectorRadius * 2) {

				DrawUtil.readPixel((int) mouseX, (int) mouseY, pixel -> {

					if (pixel != null) {
						final int r = pixel[0] & 0xff;
						final int g = pixel[1] & 0xff;
						final int b = pixel[2] & 0xff;

						option.getOriginal().setRed(r);
						option.getOriginal().setGreen(g);
						option.getOriginal().setBlue(b);

						children().forEach(e -> {
							if (e instanceof Updatable) {
								((Updatable) e).update();
							}
						});
						children().forEach(e -> {
							if (e instanceof EditBox) {
								((EditBox) e).setValue(option.get().toString().split(";")[0]);
							}
						});
					}
				});
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}
}
