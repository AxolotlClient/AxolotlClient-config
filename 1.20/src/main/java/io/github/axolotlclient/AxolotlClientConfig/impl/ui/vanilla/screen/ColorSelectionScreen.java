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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.mojang.blaze3d.glfw.Window;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.FloatOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class ColorSelectionScreen extends Screen {
	private final ColorOption option;
	private final Identifier texture = new Identifier("axolotlclientconfig", "textures/gui/colorwheel.png");
	private final Screen parent;
	private BooleanOption chroma;
	private FloatOption speed;
	private IntegerOption alpha;
	private int selectorRadius;
	private float selectorX;
	private float selectorY;
	private int buttonsX;

	public ColorSelectionScreen(Screen parent, ColorOption option) {
		super(Text.translatable("select_color"));
		this.option = option;
		this.parent = parent;
	}

	@Override
	public void init() {
		super.init();
		addDrawableSelectableElement(ButtonWidget.builder(CommonTexts.BACK, buttonWidget -> closeScreen())
			.position(width / 2 - 75, height - 40).build());

		chroma = new BooleanOption("option.chroma", option.get().isChroma(), val -> {
			option.get().setChroma(val);
		});
		speed = new FloatOption("option.speed", option.get().getChromaSpeed(), val -> {
			option.get().setChromaSpeed(val);
		}, 0f, 4f);
		alpha = new IntegerOption("option.alpha", option.get().getAlpha(), val -> {
			option.get().setAlpha(val);
			children().forEach(e -> {
				if (e instanceof TextFieldWidget) {
					((TextFieldWidget) e).setText(option.get().toString().split(";")[0]);
				}
			});
		}, 0, 255);

		selectorRadius = Math.max(Math.min(width / 4 - 10, (height) / 2 - 60), 75);
		selectorX = width / 4f - selectorRadius;
		selectorY = height / 2f - selectorRadius;

		buttonsX = (int) Math.max(width / 2f + 25, selectorX + selectorRadius * 2 + 10);

		int y = 120;
		addDrawableSelectableElement(ConfigStyles.createWidget(buttonsX, y, 150, 20, chroma));
		y += 45;
		if (height > 300) {
			addDrawableSelectableElement(ConfigStyles.createWidget(buttonsX, y, 150, 20, speed));
			y += 45;
		}
		addDrawableSelectableElement(ConfigStyles.createWidget(buttonsX, y, 150, 20, alpha));
		y += 45;
		if (this.height - 250 > 0) {
			y -= 20;
			TextFieldWidget text = new TextFieldWidget(client.textRenderer, buttonsX, y, 150, 20, Text.empty());
			text.setChangedListener(s -> {
				try {
					option.set(Color.parse(s));
					option.get().setChroma(chroma.get());
					option.get().setChromaSpeed(speed.get());

					children().forEach(e -> {
						if (e instanceof Updatable) {
							((Updatable) e).update();
						}
					});
				} catch (Throwable ignored) {
				}
			});
			text.setText(option.get().toString().split(";")[0]);
			addDrawableSelectableElement(text);
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredShadowedText(client.textRenderer, title, width / 2, 20, Colors.text().toInt());

		graphics.drawTexture(texture, (int) selectorX, (int) selectorY, 0, 0, selectorRadius * 2, selectorRadius * 2, selectorRadius * 2, selectorRadius * 2);

		DrawUtil.outlineRect(graphics, (int) selectorX, (int) selectorY, selectorRadius * 2, selectorRadius * 2, Colors.BLACK.toInt());

		graphics.drawShadowedText(client.textRenderer, Text.translatable("option.current"), buttonsX, 40, Colors.text().toInt());

		DrawUtil.fillRect(graphics, buttonsX, 55, 150, 40, option.get().get().toInt());
		DrawUtil.outlineRect(graphics, buttonsX, 55, 150, 40, Colors.BLACK.toInt());

		int y = 105;
		graphics.drawShadowedText(client.textRenderer, Text.translatable("option.chroma"), buttonsX, y, Colors.text().toInt());
		y += 45;
		if (height > 300) {
			graphics.drawShadowedText(client.textRenderer, Text.translatable("option.speed"), buttonsX, y, Colors.text().toInt());
			y += 45;
		}
		graphics.drawShadowedText(client.textRenderer, Text.translatable("option.alpha"), buttonsX, y, Colors.text().toInt());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (button == 0) {
			if (mouseX >= selectorX && mouseX <= selectorX + selectorRadius * 2 &&
				mouseY >= selectorY && mouseY <= selectorY + selectorRadius * 2) {

				final ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4);
				pixelBuffer.order(ByteOrder.nativeOrder());

				GL11.glReadPixels(toGlCoordsX(mouseX), toGlCoordsY(mouseY),
					1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);

				final int r = pixelBuffer.get(0) & 0xff;
				final int g = pixelBuffer.get(1) & 0xff;
				final int b = pixelBuffer.get(2) & 0xff;
				option.get().setRed(r);
				option.get().setGreen(g);
				option.get().setBlue(b);

				children().forEach(e -> {
					if (e instanceof Updatable) {
						((Updatable) e).update();
					}
				});
				children().forEach(e -> {
					if (e instanceof TextFieldWidget) {
						((TextFieldWidget) e).setText(option.get().toString().split(";")[0]);
					}
				});
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private int toGlCoordsX(double x) {
		Window window = MinecraftClient.getInstance().getWindow();
		return (int) (x * window.getScaleFactor());
	}

	private int toGlCoordsY(double y) {
		Window window = MinecraftClient.getInstance().getWindow();
		double scale = window.getScaleFactor();
		return Math.round((float) (MinecraftClient.getInstance().getFramebuffer().textureHeight - y * scale - scale));
	}

	@Override
	public void closeScreen() {
		client.setScreen(parent);
	}
}
