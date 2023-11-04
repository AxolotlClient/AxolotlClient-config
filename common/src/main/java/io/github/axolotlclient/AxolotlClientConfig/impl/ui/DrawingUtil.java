package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import static org.lwjgl.nanovg.NanoVG.*;

public interface DrawingUtil {

	default void pushScissor(long ctx, int x, int y, int width, int height){
		ScissoringUtil.getInstance().push(ctx, x, y, width, height);
	}
	default void popScissor(long ctx){
		ScissoringUtil.getInstance().pop(ctx);
	}

	default void fill(long ctx, float x, float y, float width, float height, Color color){
		nvgBeginPath(ctx);
		nvgFillColor(ctx, color.toNVG());
		nvgRect(ctx, x, y, width, height);
		nvgFill(ctx);
	}

	default void fillRoundedRect(long ctx, float x, float y, float width, float height, Color color, float radius){
		nvgBeginPath(ctx);
		nvgFillColor(ctx, color.toNVG());
		nvgRoundedRect(ctx, x, y, width, height, radius);
		nvgFill(ctx);
	}

	default void fillCircle(long ctx, float centerX, float centerY, Color color, float radius, float startDeg, float endDeg){
		startDeg -= 90;
		endDeg -= 90;
		nvgBeginPath(ctx);
		nvgFillColor(ctx, color.toNVG());
		nvgArc(ctx, centerX, centerY, radius, nvgDegToRad(startDeg), nvgDegToRad(endDeg), NVG_CW);
		//nvgLineTo(ctx, centerX, centerY);
		//nvgCircle(ctx, centerX, centerY, radius);
		nvgFill(ctx);
	}

	default void outline(long ctx, float x, float y, float width, float height, Color color, float lineWidth){
		nvgStrokeWidth(ctx, lineWidth);
		nvgBeginPath(ctx);
		nvgStrokeColor(ctx, color.toNVG());
		nvgMoveTo(ctx, x, y);
		nvgLineTo(ctx, x + width, y);
		nvgLineTo(ctx, x + width, y + height);
		nvgLineTo(ctx, x, y + height);
		nvgLineTo(ctx, x, y);

		nvgStroke(ctx);
	}

	default void outlineRoundedRect(long ctx, float x, float y, float width, float height, Color color, float radius, float lineWidth){
		nvgBeginPath(ctx);
		nvgStrokeColor(ctx, color.toNVG());
		//nvgPathWinding(ctx, NVG_HOLE);
		nvgRoundedRect(ctx, x, y, width, height, radius);
		nvgStrokeWidth(ctx, lineWidth);
		nvgStroke(ctx);
	}

	default void outlineCircle(long ctx, float centerX, float centerY, Color color, float radius, float lineWidth, float startDeg, float endDeg){
		startDeg -= 90;
		endDeg -= 90;
		nvgBeginPath(ctx);
		//nvgPathWinding(ctx, NVG_HOLE);
		//nvgCircle(ctx, centerX, centerY, radius);
		nvgArc(ctx, centerX, centerY, radius, nvgDegToRad(startDeg), nvgDegToRad(endDeg), NVG_CW);
		nvgStrokeColor(ctx, color.toNVG());
		nvgStrokeWidth(ctx, lineWidth);
		nvgStroke(ctx);
	}

	default NVGColor toNVGColor(int color){
		return toNVGColor(color >> 24 & 0xFF, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF);
	}

	default NVGColor toNVGColor(int alpha, int red, int green, int blue){
		return nvgRGBA((byte) red, (byte) green, (byte) blue, (byte) alpha, NVGColor.create());
	}

	default float drawString(long ctx, NVGFont font, String text, float x, float y, Color color){
		nvgFillColor(ctx, color.toNVG());
		font.bind();
		return font.renderString(text, x, y);
	}

	default float drawCenteredString(long ctx, NVGFont font, String text, float centerX, float y, Color color){
		return drawString(ctx, font, text, centerX - font.getWidth(text)/2, y, color);
	}
}
