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

package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Graphics;

public class GraphicsImpl implements Graphics {

	private int[][] data;

	public GraphicsImpl(byte[] data) {
		this.data = read(data);
	}

	public GraphicsImpl(int[][] data) {
		this.data = data;
	}

	public GraphicsImpl(int width, int height) {
		this.data = new int[height][width];
	}

	protected int[][] read(byte[] bytes) {
		try {
			try (NativeImage image = NativeImage.read(bytes)) {
				int[][] data = new int[Math.max(image.getHeight(), this.data.length)][Math.max(image.getWidth(), this.data.length)];

				for (int y = 0; y < data.length; y++) {
					for (int x = 0; x < data[y].length; x++) {
						data[y][x] = image.getPixel(x, y);
					}
				}
				return data;
			}

		} catch (IOException ignored) {
		}
		return new int[0][0];
	}

	protected NativeImage write(int[][] data) {
		AtomicInteger width = new AtomicInteger();
		Arrays.stream(data).forEach(arr -> width.set(Math.max(width.get(), arr.length)));
		int height = data.length;
		NativeImage image = new NativeImage(width.get(), height, true);
		for (int x = 0; x < width.get(); x++) {
			for (int y = 0; y < height; y++) {
				image.setPixel(x, y, data[y][x]);
			}
		}
		return image;
	}

	@Override
	public void setPixelColor(int x, int y, Color color) {
		data[y][x] = color.toInt();
	}

	@Override
	public int getPixelColor(int x, int y) {
		return data[y][x];
	}

	@Override
	public byte[] getPixelData() {
		try {
			return DrawUtil.writeToByteArray(write(data));
		} catch (IOException ignored) {
		}
		return new byte[0];
	}

	@Override
	public void setPixelData(byte[] data) {
		this.data = read(data);
	}

	@Override
	public int getWidth() {
		AtomicInteger width = new AtomicInteger();
		Arrays.stream(data).forEach(arr -> width.set(Math.max(width.get(), arr.length)));
		return width.get();
	}

	@Override
	public int getHeight() {
		return data.length;
	}
}
