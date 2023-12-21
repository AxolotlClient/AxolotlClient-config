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
import java.nio.ByteBuffer;

import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public class VariantFont extends NVGFont {
	private final int italicVariant, boldVariant, boldItalicVariant;
	private final ByteBuffer italicBuffer, boldBuffer, boldItalicBuffer;

	VariantFont(long ctx, String regularFontPath, String italicPath, String boldPath, String boldItalicPath) throws IOException {
		super(ctx, VariantFont.class.getResourceAsStream(regularFontPath));
		italicVariant = createFont(ctx, italicBuffer = mallocAndRead(VariantFont.class.getResourceAsStream(italicPath)), "italic");
		boldVariant = createFont(ctx, boldBuffer = mallocAndRead(VariantFont.class.getResourceAsStream(boldPath)), "bold");
		boldItalicVariant = createFont(ctx, boldItalicBuffer = mallocAndRead(VariantFont.class.getResourceAsStream(boldItalicPath)), "boldItalic");
	}

	public float renderString(String text, float x, float y, boolean italic, boolean bold) {
		if (italic && bold) {
			return renderStringBoldItalic(text, x, y);
		} else if (italic) {
			return renderStringItalic(text, x, y);
		} else if (bold) {
			return renderStringBold(text, x, y);
		} else {
			return super.renderString(text, x, y);
		}
	}

	public float renderStringBold(String text, float x, float y){
		return renderWithVariant(text, x, y, boldVariant);
	}

	public float renderStringItalic(String text, float x, float y){
		return renderWithVariant(text, x, y, italicVariant);
	}

	public float renderStringBoldItalic(String text, float x, float y){
		return renderWithVariant(text, x, y, boldItalicVariant);
	}

	private float renderWithVariant(String text, float x, float y, int variant) {
		NanoVG.nvgFontFaceId(nvg, variant);
		y += getLineHeight(variant);
		float result = NanoVG.nvgText(nvg, x, y, text);
		//super.bind();
		return result;
	}

	@Override
	public void close() {
		super.close();
		if (italicBuffer != null) {
			MemoryUtil.memFree(italicBuffer);
		}
		if (boldBuffer != null) {
			MemoryUtil.memFree(boldBuffer);
		}
		if (boldItalicBuffer != null) {
			MemoryUtil.memFree(boldItalicBuffer);
		}
	}

	public float getLineHeight(int variant) {
		float[] ascender = new float[1];
		float[] descender = new float[1];
		float[] lineh = new float[1];
		NanoVG.nvgTextMetrics(nvg, ascender, descender, lineh);
		return lineh[0];
	}
}
