package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import net.minecraft.client.util.math.MatrixStack;

public interface Drawable extends net.minecraft.client.gui.Drawable {

	default void render(long ctx, int mouseX, int mouseY, float delta){}

	default void render(int mouseX, int mouseY, float delta){}

	@Override
	default void render(MatrixStack matrices, int mouseX, int mouseY, float delta){}
}
