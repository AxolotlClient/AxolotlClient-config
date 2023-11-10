package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded;

import java.util.function.Consumer;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;

public class NVGUtil {

	public static void wrap(Consumer<Long> function) {
		NVGMC.wrap(function);
	}
}
