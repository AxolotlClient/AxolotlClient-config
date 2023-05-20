package io.github.axolotlclient.AxolotlClientConfig;

import io.github.axolotlclient.AxolotlClientConfig.common.Translations;
import net.minecraft.client.resource.language.I18n;

public class TranslationProvider implements Translations {

	private static final TranslationProvider Instance = new TranslationProvider();

	private TranslationProvider() {
	}

	public static TranslationProvider getInstance() {
		return Instance;
	}

	@Override
	public String translate(String key) {
		return I18n.translate(key);
	}
}
