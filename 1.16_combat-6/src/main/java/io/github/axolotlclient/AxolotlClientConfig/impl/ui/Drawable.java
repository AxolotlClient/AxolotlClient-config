package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

public interface Drawable {

	default void render(long ctx, int mouseX, int mouseY, float delta){}

	default void render(int mouseX, int mouseY, float delta){}
}
