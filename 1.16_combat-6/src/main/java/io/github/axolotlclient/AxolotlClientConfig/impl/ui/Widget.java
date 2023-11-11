package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

public interface Widget {

	int getX();

	void setX(int x);

	int getY();

	void setY(int y);

	int getWidth();

	int getHeight();

	default void setPosition(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

}
