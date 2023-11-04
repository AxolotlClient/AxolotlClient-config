package io.github.axolotlclient.AxolotlClientConfig.api.util;

import io.github.axolotlclient.AxolotlClientConfig.impl.AxolotlClientConfigImpl;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

public class Color implements Runnable {

	@Getter
	@Setter
	private int red, green, blue, alpha;

	@Getter
	private boolean chroma;

	private float chromaHue;

	private NVGColor nvgColor;

	public Color(int color) {
		this(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF);
	}

	public Color(int color, boolean chroma){
		this(color);
		setChroma(chroma);
	}

	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public Color(int r, int g, int b, boolean chroma){
		this(r, g, b);
		setChroma(chroma);
	}

	public Color(int r, int g, int b, int a) {
		this(r, g, b, a, false);
	}

	public Color(int red, int green, int blue, int alpha, boolean chroma) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		setChroma(chroma);
	}

	/**
	 * Get this color as an integer.
	 * @implNote For allowing chroma, use <code>.get().toInt()</code>
	 * @return the color packed as an integer
	 */
	public int toInt() {
		int color = getAlpha();
		color = (color << 8) + getRed();
		color = (color << 8) + getGreen();
		color = (color << 8) + getBlue();
		return color;
	}

	public NVGColor toNVG() {
		if (nvgColor == null){
			nvgColor = NanoVG.nvgRGBA((byte) getRed(), (byte) getGreen(), (byte) getBlue(), (byte) getAlpha(), NVGColor.create());
			return nvgColor;
		}
		if (nvgColor.a()*255 != getAlpha() || nvgColor.b()*255 != getBlue() || nvgColor.g()*255 != getGreen() || nvgColor.r()*255 != getRed()){
			return nvgColor.a(getAlpha()/255f).r(getRed()/255f).g(getGreen()/255f).b(getBlue()/255f);
		}
		return nvgColor;
	}

	public Color get() {
		return chroma ? withHue(chromaHue) : this;
	}

	public float[] toHSV() {
		float[] vals = new float[4];
		java.awt.Color.RGBtoHSB(getRed(), getGreen(), getBlue(), vals);
		vals[3] = getAlpha() / 255f;
		return vals;
	}

	public static Color fromHSV(float[] vals) {
		if (vals.length == 3) {
			return fromHSV(vals[0], vals[1], vals[2]);
		} else if (vals.length == 4) {
			return fromHSV(vals[0], vals[1], vals[2]).withAlpha((int) (vals[3] * 255));
		}
		throw new IllegalArgumentException();
	}

	public static Color fromHSV(float hue, float saturation, float value) {
		return new Color(java.awt.Color.HSBtoRGB(hue, saturation, value));
	}

	@Override
	public String toString() {
		return String.format("#%08X;%s", toInt(), chroma);
	}

	public static Color parse(String color) {
		try {
			return new Color(Integer.parseInt(color));
		} catch (NumberFormatException ignored) {
		}

		if (color.startsWith("#")) {
			color = color.substring(1);
		} else if (color.startsWith("0x")) {
			color = color.substring(2);
		}
		int length = color.length();
		if (color.contains(";")){
			length = color.substring(0, color.indexOf(';')).length();
		}
		if (length == 6) {
			color = "FF" + color;
		}
		length = color.length();
		if (color.contains(";")){
			length = color.substring(0, color.indexOf(';')).length();
		}
		if (length != 8) {
			throw new IllegalArgumentException();
		}

		try {
			Color c = new Color(Integer.valueOf(color.substring(2, 4), 16),
				Integer.valueOf(color.substring(4, 6), 16),
				Integer.valueOf(color.substring(6, 8), 16),
				Integer.valueOf(color.substring(0, 2), 16));
			if (color.contains(";")){
				c.setChroma(Boolean.parseBoolean(color.substring(0, color.indexOf(';'))));
			}
			return c;
		} catch (NumberFormatException error) {
			throw new IllegalArgumentException(error);
		}
	}

	public void setChroma(boolean chroma) {
		if (chroma && !this.chroma) {
			AxolotlClientConfigImpl.getInstance().registerTickListener(this);
			chromaHue = toHSV()[0];
		} else if (!chroma && this.chroma) {
			AxolotlClientConfigImpl.getInstance().removeTickListener(this);
		}
		this.chroma = chroma;
	}

	@Override
	public void run() {
		chromaHue += 0.001*(2*Math.PI);
		if (chromaHue >= 2*Math.PI) {
			chromaHue -= 2*Math.PI;
		}
	}

	public Color withRed(int red) {
		return this.red == red ? this : new Color(red, this.green, this.blue, this.alpha, this.chroma);
	}

	public Color withGreen(int green) {
		return this.green == green ? this : new Color(this.red, green, this.blue, this.alpha, this.chroma);
	}

	public Color withBlue(int blue) {
		return this.blue == blue ? this : new Color(this.red, this.green, blue, this.alpha, this.chroma);
	}

	public Color withAlpha(int alpha) {
		return this.alpha == alpha ? this : new Color(this.red, this.green, this.blue, alpha, this.chroma);
	}

	public Color withChroma(boolean chroma) {
		return this.chroma == chroma ? this : new Color(this.red, this.green, this.blue, this.alpha, chroma);
	}

	public Color withHue(float hue){
		float[] vals = toHSV();
		if (vals[0] == hue){
			return this;
		} else {
			vals[0] = hue;
			return fromHSV(vals);
		}
	}

	public Color withSaturation(float saturation){
		float[] vals = toHSV();
		if (vals[1] == saturation){
			return this;
		} else {
			vals[1] = saturation;
			return fromHSV(vals);
		}
	}

	public Color withBrightness(float brightness){
		float[] vals = toHSV();
		if (vals[2] == brightness){
			return this;
		} else {
			vals[2] = brightness;
			return fromHSV(vals);
		}
	}
}
