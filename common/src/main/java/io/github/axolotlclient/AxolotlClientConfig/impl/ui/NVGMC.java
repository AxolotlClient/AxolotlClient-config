package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import io.github.axolotlclient.AxolotlClientConfig.api.util.WindowPropertiesProvider;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;

public class NVGMC {
	private static long nvgContext;
	private static boolean initialized;
	private static WindowPropertiesProvider propertiesProvider;

	public static void setWindowPropertiesProvider(WindowPropertiesProvider provider) {
		propertiesProvider = provider;
	}

	private static void initNVG() {

		if (propertiesProvider == null) {
			throw new IllegalStateException("WindowPropertiesProvider == null");
		}

		nvgContext = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS);
		initialized = true;
	}

	public static NVGFont createFont(String regular, String italic, String bold, String boldItalic) throws IOException {
		return new VariantFont(getNvgContext(), regular, italic, bold, boldItalic);
	}

	public static NVGFont createFont(InputStream ttf) throws IOException {

		return new NVGFont(getNvgContext(), ttf);
	}

	public static NVGFont createFont(int fontHandle) {
		return new NVGFont(getNvgContext(), fontHandle);
	}

	private static long getNvgContext() {
		if (nvgContext == 0 || !initialized) {
			initNVG();
		}
		return nvgContext;
	}

	public static void wrap(Consumer<Long> run) {
		wrap(true, run);
	}

	public static void wrap(boolean scale, Consumer<Long> run) {

		long ctx = getNvgContext();

		int width = propertiesProvider.getWidth();
		int height = propertiesProvider.getHeight();
		NanoVG.nvgBeginFrame(ctx, width,
			height,
			(float) width / height);

		if (scale) {
			float factor = propertiesProvider.getScaleFactor();
			NanoVG.nvgScale(ctx, factor, factor);
		}

		run.accept(ctx);

		NanoVG.nvgEndFrame(ctx);
	}
}
