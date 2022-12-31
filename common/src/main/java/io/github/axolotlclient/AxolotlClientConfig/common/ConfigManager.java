package io.github.axolotlclient.AxolotlClientConfig.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientConfig.common.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Identifiable;

/**
 * The class handling config saving/loading.
 * Also has some common methods for category saving and loading.
 */

public interface ConfigManager {

    void save();

    default JsonObject getConfig(JsonObject object, OptionCategory category){
        for (Option<?> option : category.getOptions()) {

            object.add(getName(option), option.getJson());
        }

        if(!category.getSubCategories().isEmpty()){
            for(OptionCategory sub:category.getSubCategories()){
                JsonObject subOption;
                if(object.has(getName(category)) && object.get(getName(category)).isJsonObject()) {
                    subOption = object.get(getName(category)).getAsJsonObject();
                } else {
                    subOption = new JsonObject();
                }
                object.add(getName(sub), getConfig(subOption, sub));
            }
        }
        return object;
    }

    void load();

    default void setOptions(JsonObject config, OptionCategory category){
        for (Option<?> option : category.getOptions()) {
            if (config.has(getName(option))) {
                JsonElement part = config.get(getName(option));
                option.setValueFromJsonElement(part);
            }
        }
        if(!category.getSubCategories().isEmpty()){
            for (OptionCategory sub: category.getSubCategories()) {
                if (config.has(getName(sub))) {
                    JsonObject subCat = config.get(getName(sub)).getAsJsonObject();
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

    default String getName(Identifiable t){
        return t.getName();
    }
}
