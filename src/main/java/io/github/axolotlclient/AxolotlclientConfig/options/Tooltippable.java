package io.github.axolotlclient.AxolotlclientConfig.options;

import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

/**
 * A basic interface representing an element with a tooltip.
 * The tooltip should be located at key.tooltip
 */

public interface Tooltippable extends Identifiable {

    String getName();

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

    // Only used by the narrator currently.
    default String getStrippedTooltip(){
        return getTooltip().strip().replace("<br>", "");
    }

    default @Nullable String getTooltip(String location){
        String tooltip = getTooltipPrefix();
        if(I18n.hasTranslation(location + ".tooltip")) {
            return tooltip+I18n.translate(location+".tooltip");
        } else if(!tooltip.equals("")) {
            return tooltip;
        }
        return null;
    }
}
