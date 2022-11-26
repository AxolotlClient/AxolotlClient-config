package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public abstract class NoSaveOption<T> extends OptionBase<T> {

    public NoSaveOption(String name, T def) {
        super(name, def);
    }

    public NoSaveOption(String name, ChangedListener<T> onChange, T def) {
        super(name, onChange, def);
    }

    public NoSaveOption(String name, String tooltipKeyPrefix, T def) {
        super(name, tooltipKeyPrefix, def);
    }

    public NoSaveOption(String name, String tooltipKeyPrefix, ChangedListener<T> onChange, T def) {
        super(name, tooltipKeyPrefix, onChange, def);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {

    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive("");
    }
}