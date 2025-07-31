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

package io.github.axolotlclient.AxolotlClientConfig.impl.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonConfigManager implements ConfigManager {

	protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@Getter @Setter
	protected Path file;
	@Getter @Setter
	protected OptionCategory root;
	protected List<String> suppressedNames = new ArrayList<>();

	public JsonConfigManager(Path file, OptionCategory root) {
		this.file = file;
		this.root = root;
	}

	@Override
	public void save() {
		JsonObject object = new JsonObject();
		save(object, root);

		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, GSON.toJson(object));
		} catch (IOException e) {
			log.warn("Failed to save config: ", e);
		}
	}

	protected void save(JsonObject object, OptionCategory category) {
		for (Map.Entry<OptionCategory, Boolean> child : category.getSubCategoryMap().entrySet()) {
			var childCategory = child.getKey();
			if (child.getValue()) {
				JsonObject childObject = new JsonObject();
				save(childObject, childCategory);
				if (!childObject.entrySet().isEmpty()) {
					object.add(childCategory.getName(), childObject);
				}
			}
		}

		category.getOptionMap().forEach((o, s) -> {
			if (s) {
				String value = o.toSerializedValue();
				if (value != null) object.addProperty(o.getName(), value);
			}
		});
	}

	@Override
	public void load() {
		try {
			if (Files.exists(file)) {
				try (BufferedReader reader = Files.newBufferedReader(file)) {
					JsonObject object = GSON.fromJson(reader, JsonObject.class);

					load(root, object);
				}
				return;
			}
		} catch (IOException e) {
			log.warn("Failed to load config: ", e);
		}

		setDefaults(root);
	}

	public Collection<String> getSuppressedNames() {
		return suppressedNames;
	}

	@Override
	public void suppressName(String name) {
		suppressedNames.add(name);
	}

	protected void load(OptionCategory category, JsonObject object) {
		category.getOptionMap().forEach((option, s) -> {
			if (!s) return;
			if (object.has(option.getName())) {
				try {
					option.fromSerializedValue(object.get(option.getName()).getAsString());
				} catch (Throwable t) {
					option.setDefault();
				}
			} else {
				option.setDefault();
			}
		});
		category.getSubCategoryMap().forEach((cat, s) -> {
			if (s && object.has(cat.getName())) {
				load(cat, object.get(cat.getName()).getAsJsonObject());
			}
		});
	}

	protected void setDefaults(OptionCategory category) {
		category.getOptionMap().entrySet().stream().filter(Map.Entry::getValue)
			.map(Map.Entry::getKey).forEach(Option::setDefault);
		category.getSubCategoryMap().entrySet().stream()
			.filter(Map.Entry::getValue)
			.map(Map.Entry::getKey)
			.forEach(this::setDefaults);
	}
}
