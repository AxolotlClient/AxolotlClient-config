package io.github.axolotlclient.AxolotlclientConfig;

import com.google.gson.*;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import org.quiltmc.loader.api.QuiltLoader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * A default config manager to use by mods not implementing a custom one.
 * The modid (all lowercase) is used for the file name, in json format.
 */

public class DefaultConfigManager implements ConfigManager {
    private final List<OptionCategory> categories;
    private final String modid;
    private final Path confPath = QuiltLoader.getConfigDir().resolve(".json");

    public DefaultConfigManager(String modid){
        this.modid = modid;
        categories = AxolotlClientConfigManager.getModConfig(modid).getCategories();
    }

    public void save(){
        try{
            saveFile();
        } catch (IOException e) {
            AxolotlClientConfigManager.LOGGER.error("Failed to save config for mod: {}!", modid);
        }
    }

    private void saveFile() throws IOException {

        JsonObject config = new JsonObject();
        for(OptionCategory category : categories) {
            JsonObject object = new JsonObject();

            config.add(category.getName(), getConfig(object, category));

        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Files.write(confPath, Collections.singleton(gson.toJson(config)));
    }

    public JsonObject getConfig(JsonObject object, OptionCategory category){
        for (Option option : category.getOptions()) {

            object.add(option.getName(), option.getJson());
        }

        if(!category.getSubCategories().isEmpty()){
            for(OptionCategory sub:category.getSubCategories()){
                JsonObject subOption = new JsonObject();
                object.add(sub.getName(), getConfig(subOption, sub));
            }
        }
        return object;
    }

    public void load() {

        loadDefaults();

        try {
            JsonObject config = JsonParser.parseReader(new FileReader(confPath.toString())).getAsJsonObject();

            for(OptionCategory category:categories) {
                if(config.has(category.getName())) {
                    setOptions(config.get(category.getName()).getAsJsonObject(), category);
                }
            }
        } catch (Exception e){
            AxolotlClientConfigManager.LOGGER.error("Failed to load config for modid {}! Using default values... \nError: ", modid);
            e.printStackTrace();
        }
    }

    public void setOptions(JsonObject config, OptionCategory category){
        for (Option option : category.getOptions()) {
            if (config.has(option.getName())) {
                JsonElement part = config.get(option.getName());
                option.setValueFromJsonElement(part);
            }
        }
        if(!category.getSubCategories().isEmpty()){
            for (OptionCategory sub: category.getSubCategories()) {
                if (config.has(sub.getName())) {
                    JsonObject subCat = config.get(sub.getName()).getAsJsonObject();
                    setOptions(subCat, sub);
                }
            }
        }
    }

    public void loadDefaults(){
        AxolotlClientConfigManager.getModConfig(modid).getCategories().forEach(this::setOptionDefaults);
    }

    public void setOptionDefaults(OptionCategory category){
        category.getOptions().forEach(Option::setDefaults);
        if(!category.getSubCategories().isEmpty()){
            for (OptionCategory sub: category.getSubCategories()) {
                setOptionDefaults(sub);
            }
        }
    }
}
