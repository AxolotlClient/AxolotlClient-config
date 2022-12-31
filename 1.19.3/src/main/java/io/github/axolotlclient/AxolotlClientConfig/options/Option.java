package io.github.axolotlclient.AxolotlClientConfig.options;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.axolotlclient.AxolotlClientConfig.common.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

public interface Option<T> extends io.github.axolotlclient.AxolotlClientConfig.common.options.Option<T>, Tooltippable, WidgetSupplier {

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

    @Override
    default AxolotlClientConfigManager getConfigManager() {
        return io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager.getInstance();
    }
}
