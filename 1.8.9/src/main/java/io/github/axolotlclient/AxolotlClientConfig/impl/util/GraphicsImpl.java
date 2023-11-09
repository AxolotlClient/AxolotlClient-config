package io.github.axolotlclient.AxolotlClientConfig.impl.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

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
			BufferedImage image = readImage(bytes);
				int[][] data = new int[Math.max(image.getHeight(), this.data.length)][Math.max(image.getWidth(), this.data.length)];

				for (int y = 0; y < image.getHeight(); y++) {
					for (int x = 0; x < image.getWidth(); x++) {
						data[y][x] = image.getRGB(x, y);
					}
				}
				return data;


		} catch (IOException ignored) {
		}
		return new int[0][0];
	}

	private BufferedImage readImage(byte[] input) throws IOException {
		return ImageIO.read(new ByteArrayInputStream(input));
	}

	protected BufferedImage write(int[][] data) {
		AtomicInteger width = new AtomicInteger();
		Arrays.stream(data).forEach(arr -> width.set(Math.max(width.get(), arr.length)));
		int height = data.length;
		BufferedImage image = new BufferedImage(width.get(), height, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < width.get(); x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, data[y][x]);
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
		BufferedImage image = write(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", baos);
			return baos.toByteArray();
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
