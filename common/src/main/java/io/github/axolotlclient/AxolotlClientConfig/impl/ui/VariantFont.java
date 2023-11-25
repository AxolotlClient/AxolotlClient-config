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
		System.out.println(boldItalicVariant);
		System.out.println(boldVariant);
		System.out.println(italicVariant);
		System.out.println(handle);
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
