package io.github.axolotlclient.AxolotlClientConfig.common.options;

import com.google.gson.JsonElement;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;

public interface Option<T> extends Tooltippable {

    T get();

    void set(T value);

    String getName();

    JsonElement getJson();

    void setValueFromJsonElement(JsonElement element);

    T getDefault();
    void setDefaults();

}
