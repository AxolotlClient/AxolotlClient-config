package io.github.axolotlclient.AxolotlclientConfig.options;

import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * A basic interface representing an element with a tooltip.
 * The tooltip should be located at key.tooltip
 */

public interface Tooltippable extends Identifiable {

    default String getTooltipLocation(){
        return getName();
    }

    // If an option name needs to be cut, its full name will be put in its tooltip.
    default void setTooltipPrefix(String prefix){

    }
    default String getTooltipPrefix(){
        return "";
    }

    default String getTooltip(){
        return this.getTooltip(getTooltipLocation());
    }

    // This method is here in case someone ever ports a narrator feature to 1.8.9
    default String getStrippedTooltip(){
        return getTooltip().replace("<br>", "");
    }

    default @Nullable String getTooltip(String location){
        String translation = I18n.translate(location + ".tooltip");
        String tooltip = getTooltipPrefix();

        if(!Objects.equals(translation, location + ".tooltip")) {
            return tooltip + translation;
        } else if(!tooltip.equals("")) {
            return tooltip;
        }
        return null;
    }


}
