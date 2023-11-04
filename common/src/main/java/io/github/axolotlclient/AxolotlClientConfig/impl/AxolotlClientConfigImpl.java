package io.github.axolotlclient.AxolotlClientConfig.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import lombok.Getter;

public class AxolotlClientConfigImpl implements AxolotlClientConfig {

	@Getter
	private static final AxolotlClientConfigImpl instance = new AxolotlClientConfigImpl();

	private final Collection<Runnable> listeners = new ArrayList<>();
	@Getter
	private final HashMap<String, ConfigManager> registeredManagers = new HashMap<>();

	public void runTick(){
		listeners.forEach(Runnable::run);
	}

	@Override
	public void register(ConfigManager manager) {
		registeredManagers.put(manager.getRoot().getName(), manager);
	}

	@Override
	public ConfigManager getConfigManager(String name) {
		return registeredManagers.get(name);
	}

	public void registerTickListener(Runnable run){
		listeners.add(run);
	}

	public void removeTickListener(Runnable run){
		listeners.remove(run);
	}
}
