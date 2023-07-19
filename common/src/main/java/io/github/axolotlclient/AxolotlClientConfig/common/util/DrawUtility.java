package io.github.axolotlclient.AxolotlClientConfig.common.util;

public interface DrawUtility {

	void pushScissor(Rectangle rect);

	void popScissor();

	void pushMatrices();

	void popMatrices();

	default void drawRect(Rectangle rect, int color) {
		drawRect(rect.x, rect.y, rect.width, rect.height, color);
	}

	void drawRect(int x, int y, int width, int height, int color);

	default void drawCircle(int centerX, int centerY, int color, int radius) {
		drawCircle(centerX, centerY, color, radius, 0, 360);
	}

	void drawCircle(int centerX, int centerY, int color, int radius, int startDeg, int endDeg);

	default void drawRoundedRect(Rectangle rect, int color, int cornerRadius) {
		drawRoundedRect(rect.x, rect.y, rect.width, rect.height, color, cornerRadius);
	}

	default void drawRoundedRect(int x, int y, int width, int height, int color, int cornerRadius) {

		drawCircle(x + cornerRadius, y + cornerRadius, color, cornerRadius, 90, 180);
		drawCircle(x + width - cornerRadius, y + cornerRadius, color, cornerRadius, 0, 90);
		drawCircle(x + width - cornerRadius, y + height - cornerRadius, color, cornerRadius, 270, 360);
		drawCircle(x + cornerRadius, y + height - cornerRadius, color, cornerRadius, 180, 270);

		drawRect(x + cornerRadius, y, width - (cornerRadius * 2), cornerRadius, color);
		drawRect(x, y + cornerRadius, width, height - (cornerRadius * 2), color);
		drawRect(x + cornerRadius, y + height - cornerRadius, width - (cornerRadius * 2), cornerRadius, color);

	}

	default void outlineRoundedRect(Rectangle rect, int color, int cornerRadius) {
		outlineRoundedRect(rect.x, rect.y, rect.width, rect.height, color, cornerRadius);
	}

	default void outlineRoundedRect(int x, int y, int width, int height, int color, int cornerRadius) {
		outlineCircle(x + cornerRadius, y + cornerRadius, color, cornerRadius, 90, 180);
		outlineCircle(x + width - cornerRadius, y + cornerRadius, color, cornerRadius, 0, 90);
		outlineCircle(x + width - cornerRadius, y + height - cornerRadius, color, cornerRadius, 270, 360);
		outlineCircle(x + cornerRadius, y + height - cornerRadius, color, cornerRadius, 180, 270);

		drawRect(x + cornerRadius, y - 1, width - (cornerRadius * 2), 1, color);
		drawRect(x - 1, y + cornerRadius, 1, height - (cornerRadius * 2), color);
		drawRect(x + cornerRadius, y + height, width - (cornerRadius * 2), 1, color);
		drawRect(x + width, y + cornerRadius, 1, height - cornerRadius * 2, color);
	}

	default void outlineCircle(int centerX, int centerY, int color, int radius) {
		outlineCircle(centerX, centerY, color, radius, 0, 360);
	}

	void outlineCircle(int centerX, int centerY, int color, int radius, int startDeg, int endDeg);
}
