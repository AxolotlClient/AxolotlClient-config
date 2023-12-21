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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

public class VersionedJsonConfigManager extends JsonConfigManager {

	protected ConfigVersion version;
	protected VersionConverter converter;

	public VersionedJsonConfigManager(Path file, OptionCategory root, ConfigVersion version, VersionConverter converter) {
		super(file, root);
		this.version = version;
		this.converter = converter;
	}

	public VersionedJsonConfigManager(Path file, OptionCategory root, String version, VersionConverter converter) {
		this(file, root, version(version), converter);
	}

	public VersionedJsonConfigManager(Path file, OptionCategory root, int version, VersionConverter converter) {
		this(file, root, version(version), converter);
	}

	static ConfigVersion version(String version) {
		String[] versions = version.split("\\.", 3);
		return new ConfigVersion(Integer.parseInt(versions[0]), Integer.parseInt(versions[1]), Integer.parseInt(versions[2]));
	}

	static ConfigVersion version(int version) {
		return new ConfigVersion(version, 0, 0);
	}

	@Override
	public void save() {
		JsonObject object = new JsonObject();
		JsonObject config = new JsonObject();
		save(config, getRoot());
		object.addProperty("version", version.toString());
		object.add("config", config);

		try {
			Files.write(file, GSON.toJson(object).getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {

		}
	}

	@Override
	public void load() {
		try {
			if (Files.exists(file)) {
				try (BufferedReader reader = Files.newBufferedReader(file)) {
					JsonObject object = GSON.fromJson(reader, JsonObject.class);

					JsonObject config;
					if (object.has("config") && object.entrySet().size() == 2) {
						config = object.get("config").getAsJsonObject();
					} else {
						config = object;
					}

					if (!object.has("version") || object.entrySet().size() != 2) {
						config = this.converter.convert(version(0), this.version, getRoot(), config);
					} else {
						ConfigVersion version = version(object.get("version").getAsString());
						int i = this.version.compareTo(version);
						if (i != 0) {
							if (i < 0) {
								throw new IllegalStateException("Saved config version is newer! This is not allowed to happen!");
							} else {
								config = this.converter.convert(version, this.version, getRoot(), config);
							}
						}
					}

					load(root, config);
				}
				return;
			}
		} catch (IOException e) {

		}

		setDefaults(getRoot());
	}

	public interface VersionConverter {
		JsonObject convert(ConfigVersion from, ConfigVersion to, OptionCategory root, JsonObject oldConfig);
	}

	@Data
	public static class ConfigVersion implements Comparable<ConfigVersion> {
		private final int major, minor, patch;

		@Override
		public String toString() {
			return major + "." + minor + "." + patch;
		}

		@Override
		public int compareTo(@NotNull VersionedJsonConfigManager.ConfigVersion o) {
			int major = Integer.compare(getMajor(), o.getMajor());
			if (major != 0) {
				return major;
			}
			int minor = Integer.compare(getMinor(), o.getMinor());
			if (minor != 0) {
				return minor;
			}
			return Integer.compare(getPatch(), o.getPatch());
		}
	}
}
