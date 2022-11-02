package io.github.axolotlclient.AxolotlclientConfig.options;

import net.minecraft.client.resource.language.I18n;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represents an Element with a name, and a possible translation
 *
 */

public interface Identifiable {

    String getName();

    default String getTranslatedName(){
        return I18n.translate(this.getName());
    }

    /**
     * A simple comparator to sort elements alphabetically
     *
     */
    class AlphabeticalComparator implements Comparator<Identifiable> {

        public int compare(Identifiable s1, Identifiable s2) {
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
