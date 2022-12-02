package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public abstract class NoSaveOption<T> extends OptionBase<T> {

    public NoSaveOption(String name, T def) {
        super(name, def);
    }

    public NoSaveOption(String name, String tooltipKeyPrefix, T def) {
        super(name, tooltipKeyPrefix, def);
    }

	@Override
	public T get() {
		return getDefault();
	}

	@Override
    public void setValueFromJsonElement(JsonElement element) {

    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive("");
    }
}
