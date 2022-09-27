package io.github.axolotlclient.AxolotlclientConfig.options;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.axolotlclient.AxolotlclientConfig.util.CommandResponse;
import io.github.axolotlclient.AxolotlclientConfig.util.ConfigUtils;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

/**
 * A basic option
 * @param <T> The type of option this should be.
 */

public abstract class OptionBase<T> implements Option {

    /**
     * The current value of this option
     */
    protected T option;

    /**
     * The default value.
     */
    protected final T def;

    /**
     * This option's translation key
     */
    public String name;

    /**
     * (If set) this option's tooltip key prefix
     */
    public String tooltipKeyPrefix;

    /**
     * This option's callback to be called on changes of the value.
     * Will not be called on initial load.
     */
    protected final ChangedListener<T> changeCallback;

    public OptionBase(String name, T def){
        this(name, null, (newValue)->{}, def);
    }

    public OptionBase(String name, ChangedListener<T> onChange, T def){
        this(name, null, onChange, def);
    }

    public OptionBase(String name, String tooltipKeyPrefix, T def){
        this(name, tooltipKeyPrefix, (value)->{}, def);
    }

    public OptionBase(String name, String tooltipKeyPrefix, ChangedListener<T> onChange, T def){
        this.name=name;
        this.def = def;
        this.option = def;
        changeCallback = onChange;
        this.tooltipKeyPrefix = tooltipKeyPrefix;
    }

    public T get(){
        return option;
    }

    public void set(T value){
        option = value;
        changeCallback.onChanged(option);
    }

    public T getDefault(){
        return def;
    }

    public void setDefaults(){
        set(getDefault());
    }

    @Override
    public @Nullable String getTooltipLocation() {
        if(tooltipKeyPrefix != null)
            return tooltipKeyPrefix +"."+ name;
        else return name;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        try {
            return getTranslatedName();
        } catch (Exception ignored){}
        return getName();
    }

    public int onCommandExec(String arg){
        CommandResponse response = onCommandExecution(arg);
        ConfigUtils.sendChatMessage(Text.literal(response.response).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(response.success?Formatting.GREEN:Formatting.RED))));
        return Command.SINGLE_SUCCESS;
    }

    protected abstract CommandResponse onCommandExecution(String arg);

    public void getCommand(LiteralArgumentBuilder<QuiltClientCommandSource> builder){
        builder.then(ClientCommandManager.literal(getName())
                .then(ClientCommandManager.argument("value",
                        StringArgumentType.greedyString()).executes(ctx -> onCommandExec(StringArgumentType.getString(ctx, "value"))))
                .executes(ctx -> onCommandExec(""))
        );
    }
    public interface ChangedListener<T> {
        void onChanged(T value);
    }
}
