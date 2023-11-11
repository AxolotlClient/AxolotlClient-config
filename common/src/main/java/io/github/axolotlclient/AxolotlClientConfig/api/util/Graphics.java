package io.github.axolotlclient.AxolotlClientConfig.api.util;

public interface Graphics {

	void setPixelColor(int x, int y, Color color);

	int getPixelColor(int x, int y);

	byte[] getPixelData();

	void setPixelData(byte[] data);

	int getWidth();

	int getHeight();

}
