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

package io.github.axolotlclient.AxolotlClientConfig.api.util;

import io.github.axolotlclient.AxolotlClientConfig.impl.AxolotlClientConfigImpl;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

@SuppressWarnings("unused")
public class Color implements Runnable {

	@Getter
	@Setter
	private int red, green, blue, alpha;

	@Getter
	private boolean chroma;

	private float chromaHue;
	@Getter
	@Setter
	private float chromaSpeed;

	private NVGColor nvgColor;

	public Color(int color) {
		this(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF);
	}

	public Color(int color, boolean chroma) {
		this(color);
		if (chroma) {
			setChroma(true);
		}
	}

	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public Color(int r, int g, int b, boolean chroma) {
		this(r, g, b);
		if (chroma) {
			setChroma(true);
		}
	}

	public Color(int r, int g, int b, int a) {
		this(r, g, b, a, false);
	}

	public Color(int red, int green, int blue, int alpha, boolean chroma) {
		this(red, green, blue, alpha, chroma, 1F);
	}

	public Color(int red, int green, int blue, int alpha, boolean chroma, float chromaSpeed) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.chromaSpeed = chromaSpeed;
		if (chroma) {
			setChroma(true);
		}
	}

	public static Color fromHSB(float[] vals) {
		if (vals.length == 3) {
			return fromHSB(vals[0], vals[1], vals[2]);
		} else if (vals.length == 4) {
			return fromHSB(vals[0], vals[1], vals[2]).withAlpha((int) (vals[3] * 255));
		}
		throw new IllegalArgumentException();
	}

	public static Color fromHSB(float hue, float saturation, float brightness) {
		return new Color(java.awt.Color.HSBtoRGB(hue, saturation, brightness));
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
		if (color.contains(";")) {
			length = color.substring(0, color.indexOf(';')).length();
		}
		if (length == 6) {
			color = "FF" + color;
		}
		length = color.length();
		if (color.contains(";")) {
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
			if (color.contains(";")) {
				String[] parts = color.split(";");
				c.setChroma(Boolean.parseBoolean(parts[1]));
				c.setChromaSpeed(Float.parseFloat(parts[2]));
			}
			return c;
		} catch (NumberFormatException error) {
			throw new IllegalArgumentException(error);
		}
	}

	/**
	 * Get this color as an integer.
	 *
	 * @return the color packed as an integer
	 */
	public int toInt() {
		return toInt(get());
	}

	public static int toInt(Color c) {
		int color = c.getAlpha();
		color = (color << 8) + c.getRed();
		color = (color << 8) + c.getGreen();
		color = (color << 8) + c.getBlue();
		return color;
	}

	public NVGColor toNVG() {
		if (nvgColor == null) {
			nvgColor = NanoVG.nvgRGBA((byte) getRed(), (byte) getGreen(), (byte) getBlue(), (byte) getAlpha(), NVGColor.create());
			return nvgColor;
		}
		if (nvgColor.a() * 255 != getAlpha() || nvgColor.b() * 255 != getBlue() || nvgColor.g() * 255 != getGreen() || nvgColor.r() * 255 != getRed()) {
			return nvgColor.a(getAlpha() / 255f).r(getRed() / 255f).g(getGreen() / 255f).b(getBlue() / 255f);
		}
		return nvgColor;
	}

	public Color get() {
		return chroma ? withHue(chromaHue) : this;
	}

	public float[] toHSB() {
		float[] vals = new float[4];
		java.awt.Color.RGBtoHSB(getRed(), getGreen(), getBlue(), vals);
		vals[3] = getAlpha() / 255f;
		return vals;
	}

	@Override
	public String toString() {
		return String.format("#%08X;%s;%s", toInt(this), chroma, chromaSpeed);
	}

	public void setChroma(boolean chroma) {
		if (!chroma) {
			AxolotlClientConfigImpl.getInstance().removeTickListener(this);
		} else if (!this.chroma) {
			AxolotlClientConfigImpl.getInstance().registerTickListener(this);
			chromaHue = toHSB()[0];
		}

		this.chroma = chroma;
	}

	@Override
	public void run() {
		chromaHue += ((float) (0.001 * (2 * Math.PI)) * chromaSpeed);
		if (chromaHue >= 2 * Math.PI) {
			chromaHue -= (float) (2 * Math.PI);
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

	public Color withChromaSpeed(float speed) {
		return this.chromaSpeed == speed ? this : new Color(this.red, this.green, this.blue, this.alpha, chroma);
	}

	public Color withHue(float hue) {
		float[] vals = toHSB();
		if (vals[0] == hue) {
			return this;
		} else {
			vals[0] = hue;
			return fromHSB(vals);
		}
	}

	public Color withSaturation(float saturation) {
		float[] vals = toHSB();
		if (vals[1] == saturation) {
			return this;
		} else {
			vals[1] = saturation;
			return fromHSB(vals);
		}
	}

	public Color withBrightness(float brightness) {
		float[] vals = toHSB();
		if (vals[2] == brightness) {
			return this;
		} else {
			vals[2] = brightness;
			return fromHSB(vals);
		}
	}

	public float getHue() {
		return toHSB()[0];
	}

	public float getSaturation() {
		return toHSB()[1];
	}

	public float getBrightness() {
		return toHSB()[2];
	}

	public Color set(int red, int green, int blue, int alpha) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
		return this;
	}

	public Color setHSB(float hue, float saturation, float brightness) {
		int color = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
		set(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF);
		return this;
	}

	public Color setHSB(float[] hsb) {
		return setHSB(hsb[0], hsb[1], hsb[2]);
	}

	public Color setHue(float hue) {
		var hsb = toHSB();
		hsb[0] = hue;
		return setHSB(hsb);
	}

	public Color setSaturation(float saturation) {
		var hsb = toHSB();
		hsb[1] = saturation;
		return setHSB(hsb);
	}

	public Color setBrightness(float brightness) {
		var hsb = toHSB();
		hsb[2] = brightness;
		return setHSB(hsb);
	}

	public Color immutable() {
		return new ImmutableColor(getRed(), getGreen(), getBlue(), getAlpha(), isChroma(), getChromaSpeed());
	}

	public Color mutable() {
		return this;
	}

	/*@Override
	public Color clone() {
		try {
			Color clone = ((Color) super.clone()).mutable();
			clone.setAlpha(getAlpha());
			clone.setRed(getRed());
			clone.setGreen(getGreen());
			clone.setBlue(getBlue());
			clone.setChroma(isChroma());
			clone.setChromaSpeed(getChromaSpeed());
			clone.chromaHue = chromaHue;
			clone.nvgColor = null;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + this.red;
		result = result * PRIME + this.green;
		result = result * PRIME + this.blue;
		result = result * PRIME + this.alpha;
		//result = result * PRIME + Float.floatToIntBits(this.chromaSpeed);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Color)){
			return false;
		}

		Color other = (Color) obj;
		if (other.red != red){
			return false;
		}
		if (other.green != green){
			return false;
		}
		if (other.blue != blue){
			return false;
		}
		if (other.alpha != alpha){
			return false;
		}
		return true;//other.chromaSpeed == chromaSpeed;
	}*/

	private static class ImmutableColor extends Color {
		public ImmutableColor(int red, int green, int blue, int alpha, boolean chroma, float chromaSpeed) {
			super(red, green, blue, alpha, chroma, chromaSpeed);
		}

		@Override
		public void setGreen(int green) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public void setRed(int red) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public void setBlue(int blue) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public void setAlpha(int alpha) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public void setChroma(boolean chroma) {
		}

		@Override
		public void run() {
		}

		@Override
		public Color set(int red, int green, int blue, int alpha) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public Color setHSB(float hue, float saturation, float brightness) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public Color setHSB(float[] hsb) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public Color setHue(float hue) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public Color setSaturation(float saturation) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public Color setBrightness(float brightness) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public void setChromaSpeed(float chromaSpeed) {
			throw new UnsupportedOperationException("Immutable Color Object!");
		}

		@Override
		public Color withRed(int red) {
			return new Color(red, getGreen(), getBlue(), getAlpha());
		}

		@Override
		public Color withGreen(int green) {
			return new Color(getRed(), green, getBlue(), getAlpha());
		}

		@Override
		public Color withBlue(int blue) {
			return new Color(getRed(), getGreen(), blue, getAlpha());
		}

		@Override
		public Color withAlpha(int alpha) {
			return new Color(getRed(), getGreen(), getBlue(), alpha);
		}

		@Override
		public Color withChroma(boolean chroma) {
			return new Color(getRed(), getGreen(), getBlue(), getAlpha(), chroma);
		}

		@Override
		public Color withChromaSpeed(float speed) {
			return new Color(getRed(), getGreen(), getBlue(), getAlpha(), isChroma(), speed);
		}

		@Override
		public Color withHue(float hue) {
			float[] vals = toHSB();
			vals[0] = hue;
			return fromHSB(vals);
		}

		@Override
		public Color withSaturation(float saturation) {
			float[] vals = toHSB();
			vals[1] = saturation;
			return fromHSB(vals);
		}

		@Override
		public Color withBrightness(float brightness) {
			float[] vals = toHSB();
			vals[2] = brightness;
			return fromHSB(vals);
		}

		@Override
		public Color immutable() {
			return this;
		}

		@Override
		public Color mutable() {
			return new Color(getRed(), getGreen(), getBlue(), getAlpha(), isChroma(), getChromaSpeed());
		}
	}
}
