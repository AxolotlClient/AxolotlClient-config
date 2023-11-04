package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

public interface Drawable {

	/**
	 * This method should be used if NanoVG is used. If only vanilla methods are utilized, use the other method.
	 * @param ctx the NanoVG context pointer
	 * @param mouseX the mouse position (X)
	 * @param mouseY the mouse position (Y)
	 * @param delta the tick delta
	 */
	void render(long ctx, int mouseX, int mouseY, float delta);

	void render(int mouseX, int mouseY, float delta);
}
