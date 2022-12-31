package io.github.axolotlclient.AxolotlClientConfig.common.types;

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

    default String getTooltip(String location){
        String translation = translate(location + ".tooltip");
        String tooltip = getTooltipPrefix();

        if(!Objects.equals(translation, location + ".tooltip")) {
            return tooltip + translation;
        } else if(!tooltip.equals("")) {
            return tooltip;
        }
        return null;
    }


}
