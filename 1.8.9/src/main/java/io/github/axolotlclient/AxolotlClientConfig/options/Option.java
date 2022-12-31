package io.github.axolotlclient.AxolotlClientConfig.options;

import io.github.axolotlclient.AxolotlClientConfig.common.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;

import java.util.List;

/**
 * Represents the most basic type of option.
 * If you want to create your own, implement this interface.
 * If you want to not have to implement this basic stuff, extend {@link OptionBase}.
 */

public interface Option<T> extends io.github.axolotlclient.AxolotlClientConfig.common.options.Option<T>, Tooltippable, WidgetSupplier, ConfigPart {

    void onCommandExec(String[] args);

    List<String> getCommandSuggestions(String[] args);

    @Override
    default AxolotlClientConfigManager getConfigManager() {
        return io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager.getInstance();
    }
}
