package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

public interface Option<T> extends Tooltippable, WidgetSupplier {

    void setValueFromJsonElement(JsonElement element);

    JsonElement getJson();

    T get();

    void set(T newValue);

    T getDefault();

    default void setDefaults(){
        set(getDefault());
    }

    int onCommandExec(String arg);

    default void getCommand(LiteralArgumentBuilder<QuiltClientCommandSource> builder){
        builder.then(ClientCommandManager.literal(getName())
                .then(ClientCommandManager.argument("value",
                        StringArgumentType.greedyString()).executes(ctx -> onCommandExec(StringArgumentType.getString(ctx, "value"))))
                .executes(ctx -> onCommandExec(""))
        );
    }
}
