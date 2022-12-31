package io.github.axolotlclient.AxolotlClientConfig.common.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public interface Identifiable extends ConfigPart {

    String getName();

    default String getTranslatedName(){
        return getConfigManager().getTranslations().translate(getName());
    }

    /**
     * A simple comparator to sort elements alphabetically
     *
     */
    class AlphabeticalComparator implements Comparator<Identifiable> {

        public int compare(Identifiable s1, Identifiable s2) {
            if(s1.getTranslatedName().equals(s2.getTranslatedName())) return 0;
            String[] strings = {s1.getTranslatedName(), s2.getTranslatedName()};
            Arrays.sort(strings, Collections.reverseOrder());

            if (strings[0].equals(s1.getTranslatedName()))
                return 1;
            else
                return -1;
        }
    }
}
