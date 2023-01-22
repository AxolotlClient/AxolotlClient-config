package io.github.axolotlclient.AxolotlClientConfig.options;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.axolotlclient.AxolotlClientConfig.common.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public interface Option<T> extends io.github.axolotlclient.AxolotlClientConfig.common.options.Option<T>, Tooltippable, WidgetSupplier {

    void setValueFromJsonElement(JsonElement element);

    JsonElement getJson();

    T get();

    void set(T newValue);

    T getDefault();

    void setDefaults();

    int onCommandExec(String arg);

    default void getCommand(LiteralArgumentBuilder<FabricClientCommandSource> builder){
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
