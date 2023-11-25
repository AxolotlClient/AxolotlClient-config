package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded;

import java.io.IOException;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGFont;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;

public class NVGHolder {
	private static long ctx;
	private static NVGFont font;

	public static long getContext() {
		return ctx;
	}

	public static void setContext(long context) {
		ctx = context;
	}

	public static NVGFont getFont() {
		if (font == null) {
			try {
				font = NVGMC.createFont("/Inter-Regular.ttf", "/Inter-Italic.ttf", "/Inter-Bold.ttf", "/Inter-BoldItalic.ttf");
			} catch (IOException ignored) {

			}
		}
		return font;
	}
}
