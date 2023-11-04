package io.github.axolotlclient.AxolotlClientConfig.impl.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonConfigManager implements ConfigManager {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final Path file;
	private final OptionCategory root;


	@Override
	public void save() {
		JsonObject object = new JsonObject();
		save(object, root);

		try {
			Files.write(file, GSON.toJson(object).getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {

		}
	}

	private void save(JsonObject object, OptionCategory category){
		for (OptionCategory child : category.getSubCategories()){
			if (child.includeInParentTree()){
				JsonObject childObject = new JsonObject();
				save(childObject, child);
				if (!childObject.entrySet().isEmpty()){
					object.add(child.getName(), childObject);
				}
			}
		}

		category.getOptions().forEach(o -> object.addProperty(o.getName(), o.toSerializedValue()));
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
			e.printStackTrace();
		}

		setDefaults(root);
	}

	@Override
	public OptionCategory getRoot() {
		return root;
	}

	private void load(OptionCategory category, JsonObject object){
		category.getOptions().forEach(option -> {
			if (object.has(option.getName())) {
				option.fromSerializedValue(object.get(option.getName()).getAsString());
			} else {
				option.setDefault();
			}
		});
		category.getSubCategories().forEach(cat -> {
			if (cat.includeInParentTree() && object.has(cat.getName())){
				load(cat, object.get(cat.getName()).getAsJsonObject());
			}
		});
	}

	private void setDefaults(OptionCategory category){
		category.getOptions().forEach(Option::setDefault);
		category.getSubCategories().forEach(this::setDefaults);
	}
}
