package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

public interface Widget {

	void setX(int x);

	void setY(int y);

	int getX();

	int getY();

	int getWidth();

	int getHeight();

	default void setPosition(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

}