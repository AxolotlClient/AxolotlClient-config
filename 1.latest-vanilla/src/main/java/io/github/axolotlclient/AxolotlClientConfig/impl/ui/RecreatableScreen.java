package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.screens.Screen;

public interface RecreatableScreen {
	static Screen tryRecreate(Screen screen) {
		if (screen instanceof RecreatableScreen recreatable) {
			return recreatable.recreate();
		} else {
			return null;
		}
	}

	/**
	 * Reconstructs the screen.
	 * @return A new screen, with recreate also called on the parent
	 */
	@NotNull Screen recreate();
}
