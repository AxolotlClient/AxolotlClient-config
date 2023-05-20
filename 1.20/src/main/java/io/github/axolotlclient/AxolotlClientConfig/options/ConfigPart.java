package io.github.axolotlclient.AxolotlClientConfig.options;

import io.github.axolotlclient.AxolotlClientConfig.common.AxolotlClientConfigManager;

public interface ConfigPart extends io.github.axolotlclient.AxolotlClientConfig.common.types.ConfigPart {

	@Override
	default AxolotlClientConfigManager getConfigManager() {
		return io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager.getInstance();
	}
}
