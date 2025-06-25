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
