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

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Graphics;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Window;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.input.Keyboard;

public class GraphicsEditorScreen extends io.github.axolotlclient.AxolotlClientConfig.impl.ui.Screen implements DrawingUtil {

	private static final Color CHECKERBOARD_COLOR_1 = new Color(0xFF242424);
	private static final Color CHECKERBOARD_COLOR_2 = new Color(0xFF383838);
	private static final ColorOption colorOption = new ColorOption("current", Colors.WHITE);
	private final Screen parent;
	private final GraphicsOption option;
	private final Graphics graphics;
	private final int[] focusedPixel = new int[2];
	private int gridX;
	private int gridY;
	private int maxGridWidth;
	private int maxGridHeight;
	private int gridColumns;
	private int gridRows;
	private int pixelSize;
	private boolean keyboardInput;
	private boolean mouseDown;
	private int mouseButton;

	public GraphicsEditorScreen(Screen parent, GraphicsOption option) {
		super(I18n.translate("draw_graphics"));

		this.parent = parent;
		this.option = option;
		this.graphics = option.get();
	}

	@Override
	public void init() {
		addDrawableChild(new RoundedButtonWidget(width / 2 - 75, height - 40, I18n.translate("gui.back"),
			button -> Minecraft.getInstance().openScreen(parent)));


		gridX = 110;
		gridY = 40;

		maxGridWidth = width - 120;
		maxGridHeight = height - gridY - 42;

		gridColumns = graphics.getWidth();
		gridRows = graphics.getHeight();

		pixelSize = Math.min(maxGridHeight / gridRows, maxGridWidth / gridColumns);

		gridX = (int) (new Window(Minecraft.getInstance()).getScaledWidth() / 2 - (gridColumns * pixelSize) / 2);
		maxGridWidth = Math.min(maxGridWidth, gridColumns * pixelSize);
		maxGridHeight = Math.min(maxGridHeight, gridRows * pixelSize);

		addDrawableChild(ConfigStyles.createWidget(gridX + maxGridWidth + 10, gridY + 35, 100, 20, colorOption));
		RoundedButtonWidget clear = new RoundedButtonWidget(gridX + maxGridWidth + 10, gridY + 60,
			I18n.translate("clear_graphics"), buttonWidget -> clearGraphics());
		clear.setWidth(100);
		addDrawableChild(clear);
		addDrawableChild(new ElementSelectable(gridX, gridY, maxGridWidth, maxGridHeight));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		NVGUtil.wrap(ctx -> {
			NVGHolder.setContext(ctx);
			super.render(mouseX, mouseY, delta);

			drawCenteredString(ctx, NVGHolder.getFont(), title, width / 2f, 25, Colors.text());

			// Draw pixels
			for (int x = 0; x < gridColumns; x++) {
				for (int y = 0; y < gridRows; y++) {
					if (option.get().getPixelColor(x, y) != 0) {
						fill(ctx, gridX + x * pixelSize, gridY + y * pixelSize, pixelSize, pixelSize, new Color(option.get().getPixelColor(x, y)));
					} else {
						if (x % 2 == 0 && y % 2 == 0 || (x % 2 != 0 && y % 2 != 0)) {
							fill(ctx, gridX + x * pixelSize, gridY + y * pixelSize, pixelSize, pixelSize, CHECKERBOARD_COLOR_1);
						} else {
							fill(ctx, gridX + x * pixelSize, gridY + y * pixelSize, pixelSize, pixelSize, CHECKERBOARD_COLOR_2);
						}
					}
				}
			}

			int mouseGridX = (int) Math.floor((mouseX - gridX) / (float) pixelSize);
			int mouseGridY = (int) Math.floor((mouseY - gridY) / (float) pixelSize);

			if (mouseGridX >= 0 && mouseGridY >= 0 && mouseGridX < gridColumns && mouseGridY < gridRows && !keyboardInput) {

				if (mouseDown) {
					if (mouseButton == 0) {
						this.graphics.setPixelColor(mouseGridX, mouseGridY, colorOption.get().get());
					} else if (mouseButton == 1) {
						this.graphics.setPixelColor(mouseGridX, mouseGridY, Colors.TRANSPARENT);
					}
				}

				focusedPixel[0] = mouseGridX;
				focusedPixel[1] = mouseGridY;
			}
			outline(NVGHolder.getContext(), gridX + (pixelSize * focusedPixel[0]), gridY + (pixelSize * focusedPixel[1]), pixelSize, pixelSize, Colors.GREEN, 1);

			drawString(NVGHolder.getContext(), NVGHolder.getFont(), I18n.translate("option.current"),
				gridX + maxGridWidth + 10, gridY, Colors.text());
			fillRoundedRect(NVGHolder.getContext(), gridX + maxGridWidth + 10, gridY + 12, 100, 20, colorOption.get().get(), 5);
			outlineRoundedRect(NVGHolder.getContext(), gridX + maxGridWidth + 10, gridY + 12, 100, 20, Colors.BLACK, 5, 1);
		});
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		fillRoundedRect(NVGHolder.getContext(), 15, 15, width - 30, height - 30, Colors.background(), 12);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (mouseX >= gridX && mouseY >= gridY && mouseX <= gridX + maxGridWidth && mouseY <= gridY + maxGridHeight && !mouseDown) {
			mouseDown = true;
			this.mouseButton = button;
			keyboardInput = false;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void clearGraphics() {
		for (int x = 0; x < option.get().getWidth(); x++) {
			for (int y = 0; y < option.get().getHeight(); y++) {
				option.get().setPixelColor(x, y, Colors.TRANSPARENT);
			}
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (mouseDown) {
			mouseDown = false;
			//option.set(pixels);
			return true;
		}
		return false;
	}

	@Override
	protected void keyPressed(char c, int i) {
		if (i == 1) {
			this.minecraft.openScreen(parent);
			if (this.minecraft.screen == null) {
				this.minecraft.closeScreen();
			}
		}
		super.keyPressed(c, i);
	}

	private class ElementSelectable extends ButtonWidget {

		public ElementSelectable(int x, int y, int width, int height) {
			super(x, y, width, height, "", w -> {
			});
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			keyboardInput = true;
			if (keyCode == Keyboard.KEY_UP) {
				if (focusedPixel[1] > 0) {
					focusedPixel[1] -= 1;
					return true;
				}
			} else if (keyCode == Keyboard.KEY_DOWN) {
				if ((focusedPixel[1] + 1) * pixelSize < height) {
					focusedPixel[1] += 1;
					return true;
				}
			} else if (keyCode == Keyboard.KEY_LEFT) {
				if (focusedPixel[0] > 0) {
					focusedPixel[0] -= 1;
					return true;
				}
			} else if (keyCode == Keyboard.KEY_RIGHT) {
				if ((focusedPixel[0] + 1) * pixelSize < width) {
					focusedPixel[0] += 1;
					return true;
				}
			}
			if (keyCode == Keyboard.KEY_DELETE) {
				graphics.setPixelColor(focusedPixel[0], focusedPixel[1], Colors.TRANSPARENT);
			}
			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public void onPress() {
			graphics.setPixelColor(focusedPixel[0], focusedPixel[1], colorOption.get().get());
		}

		@Override
		public void render(int i, int j, float f) {
			if (isHovered() || isFocused()) {
				outline(NVGHolder.getContext(), gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, Colors.RED, 1);
			} else {
				outline(NVGHolder.getContext(), gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, Colors.highlight(), 1);
			}
		}

		@Override
		public SelectionType getType() {
			return isHovered() ? SelectionType.HOVERED : (isFocused() ? SelectionType.FOCUSED : SelectionType.NONE);
		}
	}
}
