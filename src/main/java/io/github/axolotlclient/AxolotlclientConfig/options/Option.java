package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.List;

/**
 * Represents the most basic type of option.
 * If you want to create your own, implement this interface.
 * If you want to not have to implement this basic stuff, extend {@link OptionBase}.
 */

public interface Option<T> extends Tooltippable, WidgetSupplier {

    void setValueFromJsonElement(JsonElement element);

    JsonElement getJson();

    T get();

    void set(T newValue);

    T getDefault();

    default void setDefaults(){
        set(getDefault());
    }

    void onCommandExec(String[] args);

    List<String> getCommandSuggestions();
}
