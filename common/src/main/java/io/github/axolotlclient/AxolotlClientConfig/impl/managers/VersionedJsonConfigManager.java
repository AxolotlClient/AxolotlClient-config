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

	@Override
	public void save() {
		JsonObject object = new JsonObject();
		JsonObject config = new JsonObject();
		save(config, root);
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
					JsonObject config = object.get("config").getAsJsonObject();

					ConfigVersion version = version(object.get("version").getAsString());
					int i = this.version.compareTo(version);
					if (i != 0){
						if (i < 0){
							throw new IllegalStateException("Saved config version is newer! This is not allowed to happen!");
						} else {
							config = this.converter.convert(version, this.version, getRoot(), config);
						}
					}

					load(root, config);
				}
				return;
			}
		} catch (IOException e) {

		}

		setDefaults(root);
	}

	@Data
	public static class ConfigVersion implements Comparable<ConfigVersion> {
		private final int major, minor, patch;

		@Override
		public String toString(){
			return major+"."+minor+"."+patch;
		}

		@Override
		public int compareTo(@NotNull VersionedJsonConfigManager.ConfigVersion o) {
			int major = Integer.compare(getMajor(), o.getMajor());
			if (major != 0){
				return major;
			}
			int minor = Integer.compare(getMinor(), o.getMinor());
			if (minor != 0){
				return minor;
			}
			return Integer.compare(getPatch(), o.getPatch());
		}
	}

	static ConfigVersion version(String version){
		String[] versions = version.split("\\.", 3);
		return new ConfigVersion(Integer.parseInt(versions[0]), Integer.parseInt(versions[1]), Integer.parseInt(versions[2]));
	}

	static ConfigVersion version(int version){
		return new ConfigVersion(version, 0, 0);
	}

	public interface VersionConverter {
		JsonObject convert(ConfigVersion from, ConfigVersion to, OptionCategory root, JsonObject oldConfig);
	}
}
