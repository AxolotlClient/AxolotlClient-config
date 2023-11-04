package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.ColorWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;

public class GraphicsEditorScreen extends Screen implements DrawingUtil {

	private final Screen parent;
	private final GraphicsOption option;
	private final int[] focusedPixel = new int[2];
	private int maxGridWidth;
	private int maxGridHeight;
	private int gridCollumns;
	private int gridRows;
	private int pixelSize;
	private int gridX;
	private int gridY;
	private boolean mouseDown;
	private int mouseButton;
	private boolean keyboardInput;

	private final ColorOption colorOption = new ColorOption("current", Colors.WHITE);

	public GraphicsEditorScreen(Screen parent, GraphicsOption option) {
		super(Text.translatable("draw_graphics"));
		this.option = option;
		this.parent = parent;
	}

	@Override
	public void init() {

		super.init();
		gridX = 110;
		gridY = 40;

		//pixels = option.get();

		maxGridWidth = width - 100;
		maxGridHeight = height - gridY*2;

		gridCollumns = option.get().getWidth();
		gridRows = option.get().getHeight();

		pixelSize = Math.min(maxGridHeight / gridRows, maxGridWidth / gridCollumns);

		gridX = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - (gridCollumns * pixelSize) / 2;
		maxGridWidth = Math.min(maxGridWidth, gridCollumns * pixelSize);
		maxGridHeight = Math.min(maxGridHeight, gridRows * pixelSize);

		addDrawableSelectableElement(new ElementSelectable(gridX, gridY, maxGridWidth, maxGridHeight));

		addDrawableSelectableElement(ButtonWidget.builder(Text.translatable("clear_graphics"),
				buttonWidget -> clearGraphics())
			.width(100).position(gridX + maxGridWidth + 10, gridY+60).build());

		addDrawableSelectableElement(new ColorWidget(gridX + maxGridWidth + 10, gridY+35, 100, 20, colorOption));

		addDrawableSelectableElement(ButtonWidget.builder(CommonTexts.BACK, buttonWidget -> closeScreen())
			.position(width/2-75, height-30).build());

		/*if (option.mayDrawHint()) {
			addDrawableChild(
				new BooleanWidget(gridX + maxGridWidth + 10, gridY + 25, 50, 20,
					new BooleanOption("showHint", option::setDrawHint, option.isDrawHint())) {
					@Override
					public boolean canHover() {
						return true;
					}

					@Override
					public Text getMessage() {
						return Text.of(this.option.getTranslatedName()).copy().append(": ").append(super.getMessage());
					}
				});
		}*/
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);

		graphics.drawCenteredShadowedText(client.textRenderer, this.title, width/2, 20, -1);

		// Draw pixels
		for (int x = 0; x < gridCollumns; x++) {
			for (int y = 0; y < gridRows; y++) {
				if (option.get().getPixelColor(x, y) != 0) {
					graphics.fill(gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, option.get().getPixelColor(x,y));
				} else {
					if (x % 2 == 0 && y % 2 == 0 || (x % 2 != 0 && y % 2 != 0)) {
						int checkerboardColor1 = 0xFF242424;
						graphics.fill(gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, checkerboardColor1);
					} else {
						int checkerboardColor2 = 0xFF383838;
						graphics.fill(gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, checkerboardColor2);
					}
				}
			}
		}

		// Draw Hint (default, but in black and outlined)
		/*if (option.isDrawHint()) {
			for (int x = 0; x < gridCollumns; x++) {
				for (int y = 0; y < gridRows; y++) {
					if (option.getDefault()[x][y] != 0) {
						DrawUtil.outlineRect(graphics, gridX + x * pixelSize, gridY + y * pixelSize, pixelSize, pixelSize, 0xFF000000);
					}
				}
			}
		}*/

		DrawUtil.outlineRect(graphics, gridX + focusedPixel[0] * pixelSize, gridY + focusedPixel[1] * pixelSize, pixelSize, pixelSize, Colors.GREEN.toInt());

		// Mouse interaction
		int mouseGridX = (int) Math.floor((mouseX - gridX) / (float) pixelSize);
		int mouseGridY = (int) Math.floor((mouseY - gridY) / (float) pixelSize);

		if (mouseGridX >= 0 && mouseGridY >= 0 && mouseGridX < gridCollumns && mouseGridY < gridRows && !keyboardInput) {

			if (mouseDown) {
				if (mouseGridX != focusedPixel[0] || mouseGridY != focusedPixel[1]) {
					if (mouseButton == 0) {
						option.get().setPixelColor(mouseGridX, mouseGridY, colorOption.get().get());
					} else if (mouseButton == 1) {
						option.get().setPixelColor(mouseGridX, mouseGridY, Colors.TRANSPARENT);
					}
				}
			}

			focusedPixel[0] = mouseGridX;
			focusedPixel[1] = mouseGridY;
		}

		DrawUtil.outlineRect(graphics, gridX + (pixelSize * focusedPixel[0]), gridY + (pixelSize*focusedPixel[1]), pixelSize, pixelSize, Colors.GREEN.toInt());

		graphics.drawShadowedText(client.textRenderer, Text.translatable("option.current"),
			gridX+maxGridWidth+10, gridY, Colors.WHITE.toInt());
		DrawUtil.fillRect(graphics, gridX+maxGridWidth+10, gridY+10, 100, 20, colorOption.get().get().toInt());
		DrawUtil.outlineRect(graphics, gridX+maxGridWidth+10, gridY+10, 100, 20, Colors.BLACK.toInt());
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
		for (int x = 0;x<option.get().getWidth();x++){
			for (int y = 0;y<option.get().getHeight();y++){
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
	public void closeScreen() {
		client.setScreen(parent);
	}

	private class ElementSelectable extends PressableWidget {

		public ElementSelectable(int x, int y, int width, int height) {
			super(x, y, width, height, Text.empty());
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			keyboardInput = true;
			if (keyCode == InputUtil.KEY_UP_CODE) {
				if (focusedPixel[1] > 0) {
					focusedPixel[1] -= 1;
					return true;
				}
			} else if (keyCode == InputUtil.KEY_DOWN_CODE) {
				if ((focusedPixel[1] + 1) * pixelSize < height) {
					focusedPixel[1] += 1;
					return true;
				}
			} else if (keyCode == InputUtil.KEY_LEFT_CODE) {
				if (focusedPixel[0] > 0) {
					focusedPixel[0] -= 1;
					return true;
				}
			} else if (keyCode == InputUtil.KEY_RIGHT_CODE) {
				if ((focusedPixel[0] + 1) * pixelSize < width) {
					focusedPixel[0] += 1;
					return true;
				}
			}
			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public void onPress() {
			option.get().setPixelColor(focusedPixel[0], focusedPixel[1], colorOption.get().get());
		}

		@Override
		public void render(GuiGraphics matrices, int i, int j, float f) {
			if (isHoveredOrFocused()) {
				DrawUtil.outlineRect(matrices, gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, Colors.RED.toInt());
			} else {
				DrawUtil.outlineRect(matrices, gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, -1);
			}
		}

		@Override
		protected void updateNarration(NarrationMessageBuilder builder) {

		}
	}
}
