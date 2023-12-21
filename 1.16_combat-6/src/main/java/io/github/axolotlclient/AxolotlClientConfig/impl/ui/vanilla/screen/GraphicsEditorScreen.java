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

import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

public class GraphicsEditorScreen extends io.github.axolotlclient.AxolotlClientConfig.impl.ui.Screen implements DrawingUtil {

	private final Screen parent;
	private final GraphicsOption option;
	private final int[] focusedPixel = new int[2];
	private final ColorOption colorOption = new ColorOption("current", Colors.WHITE);
	private int maxGridWidth;
	private int maxGridHeight;
	private int gridColumns;
	private int gridRows;
	private int pixelSize;
	private int gridX;
	private int gridY;
	private boolean mouseDown;
	private int mouseButton;
	private boolean keyboardInput;

	public GraphicsEditorScreen(Screen parent, GraphicsOption option) {
		super("draw_graphics");
		this.option = option;
		this.parent = parent;
	}

	@Override
	public void init() {

		super.init();
		gridX = 110;
		gridY = 40;

		maxGridWidth = width - 100;
		maxGridHeight = height - gridY * 2;

		gridColumns = option.get().getWidth();
		gridRows = option.get().getHeight();

		pixelSize = Math.min(maxGridHeight / gridRows, maxGridWidth / gridColumns);

		gridX = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - (gridColumns * pixelSize) / 2;
		maxGridWidth = Math.min(maxGridWidth, gridColumns * pixelSize);
		maxGridHeight = Math.min(maxGridHeight, gridRows * pixelSize);

		addDrawableChild(new ElementSelectable(gridX, gridY, maxGridWidth, maxGridHeight));

		addDrawableChild(new VanillaButtonWidget(gridX + maxGridWidth + 10, gridY + 60, 100, 20,
			new TranslatableText("clear_graphics"),
			buttonWidget -> clearGraphics()));

		addDrawableChild(ConfigStyles.createWidget(gridX + maxGridWidth + 10, gridY + 35, 100, 20, colorOption));

		addDrawableChild(new VanillaButtonWidget(width / 2 - 75, height - 30, 150, 20,
			ScreenTexts.BACK, buttonWidget -> removed()));
	}

	@Override
	public void render(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, delta);

		drawCenteredString(graphics, client.textRenderer, this.title, width / 2, 20, -1);

		// Draw pixels
		for (int x = 0; x < gridColumns; x++) {
			for (int y = 0; y < gridRows; y++) {
				if (option.get().getPixelColor(x, y) != 0) {
					fill(graphics, gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, option.get().getPixelColor(x, y));
				} else {
					if (x % 2 == 0 && y % 2 == 0 || (x % 2 != 0 && y % 2 != 0)) {
						int checkerboardColor1 = 0xFF242424;
						fill(graphics, gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, checkerboardColor1);
					} else {
						int checkerboardColor2 = 0xFF383838;
						fill(graphics, gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, checkerboardColor2);
					}
				}
			}
		}

		DrawUtil.outlineRect(graphics, gridX + focusedPixel[0] * pixelSize, gridY + focusedPixel[1] * pixelSize, pixelSize, pixelSize, Colors.GREEN.toInt());

		// Mouse interaction
		int mouseGridX = (int) Math.floor((mouseX - gridX) / (float) pixelSize);
		int mouseGridY = (int) Math.floor((mouseY - gridY) / (float) pixelSize);

		if (mouseGridX >= 0 && mouseGridY >= 0 && mouseGridX < gridColumns && mouseGridY < gridRows && !keyboardInput) {

			if (mouseDown) {
				if (mouseButton == 0) {
					option.get().setPixelColor(mouseGridX, mouseGridY, colorOption.get().get());
				} else if (mouseButton == 1) {
					option.get().setPixelColor(mouseGridX, mouseGridY, Colors.TRANSPARENT);
				}
			}

			focusedPixel[0] = mouseGridX;
			focusedPixel[1] = mouseGridY;
		}

		DrawUtil.outlineRect(graphics, gridX + (pixelSize * focusedPixel[0]), gridY + (pixelSize * focusedPixel[1]), pixelSize, pixelSize, Colors.GREEN.toInt());

		drawTextWithShadow(graphics, client.textRenderer, new TranslatableText("option.current"),
			gridX + maxGridWidth + 10, gridY, Colors.text().toInt());
		DrawUtil.fillRect(graphics, gridX + maxGridWidth + 10, gridY + 10, 100, 20, colorOption.get().get().toInt());
		DrawUtil.outlineRect(graphics, gridX + maxGridWidth + 10, gridY + 10, 100, 20, Colors.BLACK.toInt());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (mouseX >= gridX && mouseY >= gridY && mouseX <= gridX + maxGridWidth && mouseY <= gridY + maxGridHeight && !mouseDown) {
			mouseDown = true;
			mouseButton = button;
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
			return true;
		}
		return false;
	}

	@Override
	public void removed() {
		client.openScreen(parent);
	}

	private class ElementSelectable extends AbstractPressableButtonWidget {

		public ElementSelectable(int x, int y, int width, int height) {
			super(x, y, width, height, LiteralText.EMPTY);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			keyboardInput = true;
			if (keyCode == GLFW.GLFW_KEY_UP) {
				if (focusedPixel[1] > 0) {
					focusedPixel[1] -= 1;
					return true;
				}
			} else if (keyCode == GLFW.GLFW_KEY_DOWN) {
				if ((focusedPixel[1] + 1) * pixelSize < height) {
					focusedPixel[1] += 1;
					return true;
				}
			} else if (keyCode == GLFW.GLFW_KEY_LEFT) {
				if (focusedPixel[0] > 0) {
					focusedPixel[0] -= 1;
					return true;
				}
			} else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
				if ((focusedPixel[0] + 1) * pixelSize < width) {
					focusedPixel[0] += 1;
					return true;
				}
			}
			if (keyCode == GLFW.GLFW_KEY_DELETE) {
				option.get().setPixelColor(focusedPixel[0], focusedPixel[1], Colors.TRANSPARENT);
			}
			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public void onPress() {
			option.get().setPixelColor(focusedPixel[0], focusedPixel[1], colorOption.get().get());
		}

		@Override
		public void render(MatrixStack matrices, int i, int j, float f) {
			if (isHovered() || isFocused()) {
				DrawUtil.outlineRect(matrices, gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, Colors.RED.toInt());
			} else {
				DrawUtil.outlineRect(matrices, gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, -1);
			}
		}
	}
}
