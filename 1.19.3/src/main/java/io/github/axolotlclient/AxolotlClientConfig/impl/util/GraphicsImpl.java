package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import com.mojang.blaze3d.texture.NativeImage;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Graphics;
import org.lwjgl.system.MemoryStack;

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
			try (NativeImage image = readImage(bytes)) {
				int[][] data = new int[Math.max(image.getHeight(), this.data.length)][Math.max(image.getWidth(), this.data.length)];

				for (int y = 0; y < image.getHeight(); y++) {
					for (int x = 0; x < image.getWidth(); x++) {
						data[y][x] = image.getPixelColor(x, y);
					}
				}
				return data;
			}

		} catch (IOException ignored) {
		}
		return new int[0][0];
	}

	private NativeImage readImage(byte[] input) throws IOException {
		NativeImage var3;
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = memoryStack.malloc(input.length);
			byteBuffer.put(input);
			byteBuffer.rewind();
			var3 = NativeImage.read(byteBuffer);
		}

		return var3;
	}

	protected NativeImage write(int[][] data) {
		AtomicInteger width = new AtomicInteger();
		Arrays.stream(data).forEach(arr -> width.set(Math.max(width.get(), arr.length)));
		int height = data.length;
		NativeImage image = new NativeImage(width.get(), height, true);
		for (int x = 0; x < width.get(); x++) {
			for (int y = 0; y < height; y++) {
				image.setPixelColor(x, y, data[y][x]);
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
			try (NativeImage image = write(data)) {
				return image.getBytes();
			}
		} catch (IOException ignored) {
		}
		return new byte[1];
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
