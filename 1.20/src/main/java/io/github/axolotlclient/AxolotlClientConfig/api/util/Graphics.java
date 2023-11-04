package io.github.axolotlclient.AxolotlClientConfig.api.util;

public interface Graphics {

	void setPixelColor(int x, int y, Color color);
	int getPixelColor(int x, int y);

	void setPixelData(byte[] data);

	byte[] getPixelData();

	int getWidth();
	int getHeight();

}
