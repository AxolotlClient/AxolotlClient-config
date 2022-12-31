package io.github.axolotlclient.AxolotlClientConfig.options;

import io.github.axolotlclient.AxolotlClientConfig.common.commands.CommandResponse;
import io.github.axolotlclient.AxolotlClientConfig.util.ConfigUtils;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * A basic option
 * @param <T> The type of option this should be.
 */

public abstract class OptionBase<T> extends io.github.axolotlclient.AxolotlClientConfig.common.options.OptionBase<T> implements Option<T> {

    public OptionBase(String name, T def) {
        super(name, def);
    }

    public OptionBase(String name, ChangedListener<T> onChange, T def) {
        super(name, onChange, def);
    }

    public OptionBase(String name, String tooltipKeyPrefix, T def) {
        super(name, tooltipKeyPrefix, def);
    }

    public OptionBase(String name, String tooltipKeyPrefix, ChangedListener<T> onChange, T def) {
        super(name, tooltipKeyPrefix, onChange, def);
    }

    public void onCommandExec(String[] args){
        CommandResponse response = onCommandExecution(args);
        ConfigUtils.sendChatMessage( new LiteralText((response.success ? Formatting.GREEN : Formatting.RED) + response.response));
    }

    protected abstract CommandResponse onCommandExecution(String[] args);

    public abstract List<String> getCommandSuggestions(String[] args);
}
