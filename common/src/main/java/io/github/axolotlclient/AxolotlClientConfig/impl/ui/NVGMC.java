/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import io.github.axolotlclient.AxolotlClientConfig.api.util.WindowPropertiesProvider;
import lombok.Getter;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;

public class NVGMC {
	private static long nvgContext;
	private static boolean initialized;
	private static WindowPropertiesProvider propertiesProvider;
	@Getter
	private static boolean active;

	public static void setWindowPropertiesProvider(WindowPropertiesProvider provider) {
		propertiesProvider = provider;
	}

	private static void initNVG() {

		if (propertiesProvider == null) {
			throw new IllegalStateException("WindowPropertiesProvider == null");
		}

		nvgContext = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);
		initialized = true;
	}

	public static NVGFont createFont(InputStream regular, InputStream italic, InputStream bold, InputStream boldItalic) throws IOException {
		return new VariantFont(getNvgContext(), regular, italic, bold, boldItalic);
	}

	public static NVGFont createFont(String regular, String italic, String bold, String boldItalic) throws IOException {
		return createFont(NVGMC.class.getResourceAsStream(regular), NVGMC.class.getResourceAsStream(italic), NVGMC.class.getResourceAsStream(bold), NVGMC.class.getResourceAsStream(boldItalic));
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

		boolean separate = active;
		if (!separate) {
			startFrame(scale);
		}

		run.accept(ctx);

		if (!separate) {
			endFrame();
		}
	}

	public static void startFrame() {
		startFrame(true);
	}

	public static void startFrame(boolean scale) {
		long ctx = getNvgContext();
		int width = propertiesProvider.getWidth();
		int height = propertiesProvider.getHeight();
		NanoVG.nvgBeginFrame(ctx, width,
			height,
			(float) width / height);
		active = true;
		if (scale) {
			float factor = propertiesProvider.getScaleFactor();
			NanoVG.nvgScale(ctx, factor, factor);
		}
	}

	public static void endFrame() {
		if (active) {
			active = false;
			NanoVG.nvgEndFrame(getNvgContext());
		}
	}
}
