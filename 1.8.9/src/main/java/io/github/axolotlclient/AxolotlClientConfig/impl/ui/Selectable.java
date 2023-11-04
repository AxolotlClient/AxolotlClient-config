package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface Selectable {

	Selectable.SelectionType getType();

	default boolean isNarratable() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public static enum SelectionType {
		NONE,
		HOVERED,
		FOCUSED;

		private SelectionType() {
		}

		public boolean isFocused() {
			return this == FOCUSED;
		}
	}
}
