package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

public interface ParentElement extends Element, net.minecraft.client.gui.ParentElement {

	@Override
	default boolean mouseClicked(double mouseX, double mouseY, int button) {
		return net.minecraft.client.gui.ParentElement.super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	default boolean mouseReleased(double mouseX, double mouseY, int button) {
		return net.minecraft.client.gui.ParentElement.super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return net.minecraft.client.gui.ParentElement.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	default boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return net.minecraft.client.gui.ParentElement.super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return net.minecraft.client.gui.ParentElement.super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	default boolean charTyped(char chr, int modifiers) {
		return net.minecraft.client.gui.ParentElement.super.charTyped(chr, modifiers);
	}

	@Override
	default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return net.minecraft.client.gui.ParentElement.super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	default boolean isMouseOver(double mouseX, double mouseY) {
		return net.minecraft.client.gui.ParentElement.super.isMouseOver(mouseX, mouseY);
	}

	@Override
	default void setFocused(boolean focused) {

	}

	@Override
	default boolean isFocused() {
		return false;
	}


}
