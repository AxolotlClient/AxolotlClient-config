package io.github.axolotlclient.AxolotlClientConfig.common.util;

public interface DrawUtility {

	void pushScissor(Rectangle rect);

	void popScissor();

	void pushMatrices();

	void popMatrices();

	void fill(float x, float y, float x2, float y2, int color);

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

		float lineWidth = 0.5F;
		fill(x + cornerRadius, y, x + width - (cornerRadius), y+lineWidth, color);
		fill(x, y + cornerRadius, x+lineWidth, y + height - (cornerRadius), color);
		fill(x + cornerRadius, y + height, x + width - (cornerRadius), y+height-lineWidth, color);
		fill(x + width, y + cornerRadius, x+width-lineWidth, y + height - cornerRadius, color);
	}

	default void outlineCircle(int centerX, int centerY, int color, int radius) {
		outlineCircle(centerX, centerY, color, radius, 0, 360);
	}

	void outlineCircle(int centerX, int centerY, int color, int radius, int startDeg, int endDeg);

	void drawRect(Rectangle rect, int color, int cornerRadiusIfRounded);

	void outlineRect(Rectangle rect, int color, int cornerRadiusIfRounded);
}
