package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

public abstract class ButtonWidget extends ClickableWidget {

	private final PressAction action;

	public ButtonWidget(int x, int y, int width, int height, String message, PressAction action) {
		super(x, y, width, height, message);
		this.action = action;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		onPress();
	}

	public void onPress() {
		action.onPress(this);
	}

	public interface PressAction {
		void onPress(ButtonWidget widget);
	}
}
