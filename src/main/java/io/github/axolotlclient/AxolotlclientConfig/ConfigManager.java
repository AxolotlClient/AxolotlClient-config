package io.github.axolotlclient.AxolotlclientConfig;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;

public interface ConfigManager {

    void save();

    default JsonObject getConfig(JsonObject object, OptionCategory category){
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

    void load();

    default void setOptions(JsonObject config, OptionCategory category){
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

    void loadDefaults();

    default void setOptionDefaults(OptionCategory category){
        category.getOptions().forEach(Option::setDefaults);
        if(!category.getSubCategories().isEmpty()){
            for (OptionCategory sub: category.getSubCategories()) {
                setOptionDefaults(sub);
            }
        }
    }
}
