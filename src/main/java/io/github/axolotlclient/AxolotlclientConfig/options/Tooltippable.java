package io.github.axolotlclient.AxolotlclientConfig.options;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * A basic interface representing an element with a tooltip.
 * The tooltip should be located at key.tooltip
 */

public interface Tooltippable {

    String getName();

    default String getTooltipLocation(){
        return getName();
    }

    default String getTooltip(){
        return this.getTooltip(getTooltipLocation());
    }

    default String getStrippedTooltip(){
        return getTooltip().strip().replace("<br>", "");
    }

    default @Nullable String getTooltip(String location){
        String translation = Text.translatable(location + ".tooltip").getString();
        if(!Objects.equals(translation, location + ".tooltip")) {
            return translation;
        }
        return null;
    }

    /**
     * A simple comparator to sort elements alphabetically
     */

    class AlphabeticalComparator implements Comparator<Tooltippable> {

        // Function to compare
        public int compare(Tooltippable s1, Tooltippable s2) {
            if(s1.toString().equals(s2.toString())) return 0;
            String[] strings = {s1.toString(), s2.toString()};
            Arrays.sort(strings, Collections.reverseOrder());

            if (strings[0].equals(s1.toString()))
                return 1;
            else
                return -1;
        }
    }
}
