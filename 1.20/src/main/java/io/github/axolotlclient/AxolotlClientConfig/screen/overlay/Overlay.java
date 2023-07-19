package io.github.axolotlclient.AxolotlClientConfig.screen.overlay;

import com.mojang.blaze3d.glfw.Window;
import com.mojang.blaze3d.platform.InputUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.util.Rectangle;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Overlay extends Screen {

	private Rectangle overlay;
	private final OptionsScreenBuilder parent;

	protected Overlay(String title, OptionsScreenBuilder parent) {
		super(Text.translatable(title));
		this.parent = parent;
	}

	@Override
	protected void init() {
		Window window = MinecraftClient.getInstance().getWindow();
		width = window.getScaledWidth() - 200;
		height = window.getScaledHeight() - 100;

		overlay = new Rectangle(100, 50, width, height);

		addSelectableChild(parent.backButton);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		parent.render(graphics, mouseX, mouseY, delta);

		graphics.getMatrices().push();
		graphics.getMatrices().translate(0, 0, 200);

		DrawUtil.getInstance().drawRect(graphics, overlay, Color.DARK_GRAY.withAlpha(127).getAsInt(), 12);
		DrawUtil.getInstance().outlineRect(graphics, overlay, Color.WHITE.getAsInt(), 12);

		graphics.drawCenteredShadowedText(MinecraftClient.getInstance().textRenderer, title, MinecraftClient.getInstance().getWindow().getScaledWidth() / 2, 54, -1);

		RenderSystem.setShaderColor(1, 1, 1, 1);

		super.render(graphics, mouseX, mouseY, delta);
	}

	protected void postRender(GuiGraphics graphics) {
		graphics.getMatrices().pop();
	}

	@Override
	public void tick() {
		parent.tick();
	}

	public boolean onClick(double mouseX, double mouseY) {
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (parent.backButton.isMouseOver(mouseX, mouseY)) {
			parent.backButton.playDownSound(MinecraftClient.getInstance().getSoundManager());
			parent.closeOverlay();
			AxolotlClientConfigManager.getInstance().saveCurrentConfig();
			return true;
		}
		if (!overlay.isMouseOver(mouseX, mouseY)) {
			parent.closeOverlay();
			AxolotlClientConfigManager.getInstance().saveCurrentConfig();
			return true;
		}
		return onClick(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == InputUtil.KEY_ESCAPE_CODE) {
			parent.closeOverlay();
			AxolotlClientConfigManager.getInstance().saveCurrentConfig();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		parent.resize(client, width, height);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return super.mouseScrolled(mouseX, mouseY, amount) || parent.mouseScrolled(mouseX, mouseY, amount);
	}

	public String getModId() {
		return parent.modid;
	}
}
