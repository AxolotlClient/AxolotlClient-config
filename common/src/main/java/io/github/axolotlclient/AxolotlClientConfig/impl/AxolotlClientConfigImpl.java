/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

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

	public void runTick() {
		listeners.forEach(Runnable::run);
	}

	@Override
	public void register(ConfigManager manager) {
		registeredManagers.put(manager.getRoot().getName(), manager);
	}

	@Override
	public ConfigManager getConfigManager(OptionCategory category) {
		for (ConfigManager manager : registeredManagers.values()){
			if (findCategory(manager.getRoot(), category)){
				return manager;
			}
		}
		throw new IllegalStateException("Category "+category.getName()+" is not in any registered ConfigManager!");
	}

	@Override
	public ConfigManager getConfigManager(String name) {
		return registeredManagers.get(name);
	}

	@Override
	public void saveAll() {
		registeredManagers.values().forEach(ConfigManager::save);
	}

	@Override
	public void save(OptionCategory category) {
		getConfigManager(category).save();
	}

	private boolean findCategory(OptionCategory root, OptionCategory category){
		if (root.equals(category)){
			return true;
		}
		boolean found = false;
		for (OptionCategory sub : root.getSubCategories()){
			found = sub.includeInParentTree() && findCategory(sub, category);
			if (found) {
				break;
			}
		}
		return found;
	}

	public void registerTickListener(Runnable run) {
		listeners.add(run);
	}

	public void removeTickListener(Runnable run) {
		listeners.remove(run);
	}
}
