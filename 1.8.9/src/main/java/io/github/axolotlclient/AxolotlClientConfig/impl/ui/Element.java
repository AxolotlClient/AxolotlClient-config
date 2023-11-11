package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

public interface Element extends DrawingUtil {

	default boolean mouseClicked(double mouseX, double mouseY, int button) {
		return false;
	}

	default boolean mouseReleased(double mouseX, double mouseY, int button) {
		return false;
	}

	default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}

	default boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
		return false;
	}

	default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	default boolean charTyped(char chr, int modifiers) {
		return false;
	}

	default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	default boolean isMouseOver(double mouseX, double mouseY) {
		return false;
	}

	boolean isFocused();

	void setFocused(boolean focused);
}
