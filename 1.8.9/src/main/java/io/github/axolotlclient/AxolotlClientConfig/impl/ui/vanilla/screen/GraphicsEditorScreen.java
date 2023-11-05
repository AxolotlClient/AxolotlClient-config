package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import org.lwjgl.input.Keyboard;

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

		gridX = (int) (new Window(MinecraftClient.getInstance()).getScaledWidth() / 2 - (gridColumns * pixelSize) / 2);
		maxGridWidth = Math.min(maxGridWidth, gridColumns * pixelSize);
		maxGridHeight = Math.min(maxGridHeight, gridRows * pixelSize);

		addDrawableChild(new ElementSelectable(gridX, gridY, maxGridWidth, maxGridHeight));

		addDrawableChild(new VanillaButtonWidget(gridX + maxGridWidth + 10, gridY + 60, 100, 20,
			I18n.translate("clear_graphics"),
				buttonWidget -> clearGraphics()));

		addDrawableChild(ConfigStyles.createWidget(gridX + maxGridWidth + 10, gridY + 35, 100, 20, colorOption));

		addDrawableChild(new VanillaButtonWidget(width / 2 - 75, height - 30, 150, 20,
			I18n.translate("gui.back"), buttonWidget -> client.setScreen(parent)));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		super.render(mouseX, mouseY, delta);

		drawCenteredString(client.textRenderer, this.title, width / 2, 20, -1);

		// Draw pixels
		for (int x = 0; x < gridColumns; x++) {
			for (int y = 0; y < gridRows; y++) {
				if (option.get().getPixelColor(x, y) != 0) {
					fill(gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, option.get().getPixelColor(x, y));
				} else {
					if (x % 2 == 0 && y % 2 == 0 || (x % 2 != 0 && y % 2 != 0)) {
						int checkerboardColor1 = 0xFF242424;
						fill(gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, checkerboardColor1);
					} else {
						int checkerboardColor2 = 0xFF383838;
						fill(gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, checkerboardColor2);
					}
				}
			}
		}

		DrawUtil.outlineRect(gridX + focusedPixel[0] * pixelSize, gridY + focusedPixel[1] * pixelSize, pixelSize, pixelSize, Colors.GREEN.toInt());

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

		DrawUtil.outlineRect(gridX + (pixelSize * focusedPixel[0]), gridY + (pixelSize * focusedPixel[1]), pixelSize, pixelSize, Colors.GREEN.toInt());

		drawWithShadow(client.textRenderer, I18n.translate("option.current"),
			gridX + maxGridWidth + 10, gridY, Colors.WHITE.toInt());
		DrawUtil.fillRect(gridX + maxGridWidth + 10, gridY + 10, 100, 20, colorOption.get().get().toInt());
		DrawUtil.outlineRect(gridX + maxGridWidth + 10, gridY + 10, 100, 20, Colors.BLACK.toInt());
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

	private class ElementSelectable extends ButtonWidget {

		public ElementSelectable(int x, int y, int width, int height) {
			super(x, y, width, height, "", w -> {});
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
				option.get().setPixelColor(focusedPixel[0], focusedPixel[1], Colors.TRANSPARENT);
			}
			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public void onPress() {
			option.get().setPixelColor(focusedPixel[0], focusedPixel[1], colorOption.get().get());
		}

		@Override
		public void render(int i, int j, float f) {
			if (isHovered() || isFocused()) {
				DrawUtil.outlineRect(gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, Colors.RED.toInt());
			} else {
				DrawUtil.outlineRect(gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, -1);
			}
		}
	}
}