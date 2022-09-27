package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

public interface Option extends Tooltippable {

    String getName();

    default String getTranslatedName(){
        return Text.translatable(this.getName()).getString();
    }

    void setValueFromJsonElement(JsonElement element);

    void setDefaults();

    JsonElement getJson();
}
