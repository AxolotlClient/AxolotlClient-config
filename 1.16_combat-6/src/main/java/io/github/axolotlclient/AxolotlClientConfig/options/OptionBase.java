package io.github.axolotlclient.AxolotlClientConfig.options;

import com.mojang.brigadier.Command;
import io.github.axolotlclient.AxolotlClientConfig.common.commands.CommandResponse;
import io.github.axolotlclient.AxolotlClientConfig.util.ConfigUtils;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

/**
 * A basic option
 * @param <T> The type of option this should be.
 */

public abstract class OptionBase<T> extends io.github.axolotlclient.AxolotlClientConfig.common.options.OptionBase<T> implements Option<T> {

    public OptionBase(String name, T def) {
        super(name, def);
    }

    public OptionBase(String name, io.github.axolotlclient.AxolotlClientConfig.common.options.OptionBase.ChangedListener<T> onChange, T def) {
        super(name, onChange, def);
    }

    public OptionBase(String name, String tooltipKeyPrefix, T def) {
        super(name, tooltipKeyPrefix, def);
    }

    public OptionBase(String name, String tooltipKeyPrefix, io.github.axolotlclient.AxolotlClientConfig.common.options.OptionBase.ChangedListener<T> onChange, T def) {
        super(name, tooltipKeyPrefix, onChange, def);
    }

    public int onCommandExec(String arg){
        CommandResponse response = onCommandExecution(arg);
        ConfigUtils.sendChatMessage(new LiteralText(response.response).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(response.success?Formatting.GREEN:Formatting.RED))));
        return Command.SINGLE_SUCCESS;
    }

    protected abstract CommandResponse onCommandExecution(String arg);
}
