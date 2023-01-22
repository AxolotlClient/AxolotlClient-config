package io.github.axolotlclient.AxolotlClientConfig.options;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.CategoryWidget;
import io.github.axolotlclient.AxolotlClientConfig.util.ConfigUtils;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class OptionCategory extends io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory implements Tooltippable, WidgetSupplier, ConfigPart {

    public OptionCategory(String key) {
        super(key);
    }

    public OptionCategory(String key, boolean registerCommand) {
        super(key, registerCommand);
    }

    public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(){
        LiteralArgumentBuilder<FabricClientCommandSource> builder = ClientCommandManager.literal(getName());
        for(io.github.axolotlclient.AxolotlClientConfig.common.options.Option<?> o:getOptions()){
            ((Option<?>)o).getCommand(builder);
        }
        builder.executes(context -> {
            StringBuilder string = new StringBuilder();
            for (io.github.axolotlclient.AxolotlClientConfig.common.options.Option<?> o : getOptions()) {
                string.append("    ").append(Formatting.AQUA).append(o.getName()).append(": ").append(o.get()).append("\n");
            }
            ConfigUtils.sendChatMessage(new LiteralText(Formatting.BLUE + "Values in this category are: \n" + string));
            return Command.SINGLE_SUCCESS;
        });
        return builder;
    }

    @Override
    public AbstractPressableButtonWidget getWidget(int x, int y, int width, int height) {
        return new CategoryWidget(this, x, y, width, height);
    }

    @Override
    public void registerCommand() {
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> ClientCommandManager.DISPATCHER.register(buildCommand()));
    }
}
