package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen.RoundedConfigScreen;

public class RoundedButtonWidget extends ButtonWidget {
	public RoundedButtonWidget(int x, int y, int width, int height, String message, PressAction action) {
		super(x, y, width, height, message, action);
	}

	@Override
	protected void drawWidget(long ctx, int mouseX, int mouseY, float delta) {
		fillRoundedRect(ctx, getX(), getY(), getWidth(), getHeight(), getWidgetColor(), getHeight()/2);
		drawCenteredString(ctx, RoundedConfigScreen.font, getMessage(), getX()+getWidth()/2f,
			getY()+getHeight()/2f-RoundedConfigScreen.font.getLineHeight()/2, Colors.WHITE);
	}

	protected Color getWidgetColor(){
		return isHovered() ? Colors.DARK_YELLOW : Colors.TURQUOISE;
	}
}
