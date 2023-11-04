package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

public interface Element extends DrawingUtil, net.minecraft.client.gui.Element {

	void setFocused(boolean focused);

	boolean isFocused();
}
