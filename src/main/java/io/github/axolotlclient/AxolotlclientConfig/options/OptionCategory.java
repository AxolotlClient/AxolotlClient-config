package io.github.axolotlclient.AxolotlclientConfig.options;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.CategoryWidget;
import io.github.axolotlclient.AxolotlclientConfig.util.ConfigUtils;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OptionCategory implements Tooltippable, WidgetSupplier {

    private final String name;
    private final List<Option<?>> options = new ArrayList<>();
    private final List<OptionCategory> subCategories = new ArrayList<>();

    public OptionCategory(String key){
       this(key, true);
    }

    public OptionCategory(String key, boolean registerCommand){
        this.name = key;
        if(registerCommand) {
            try {
                ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> ClientCommandManager.getDispatcher().register(buildCommand()));
            } catch (NoClassDefFoundError ignored){}
        }
    }

    public List<Option<?>> getOptions(){
        return options;
    }

    public void add(Option<?> option){
        options.add(option);
    }

    public void add(Option<?>... options){
        Collections.addAll(this.options, options);
    }

    public void add(Collection<? extends Option<?>> options){
        this.options.addAll(options);
    }

    public void add(OptionCategory category){
        subCategories.add(category);
    }

    public void addSubCategory(OptionCategory category){
        subCategories.add(category);
    }

    public OptionCategory addSubCategories(List<OptionCategory> categories){
        subCategories.addAll(categories);
        return this;
    }

    public List<OptionCategory> getSubCategories(){
        return subCategories;
    }

    public void clearOptions(){
        options.clear();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        try {
            return getTranslatedName();
        } catch (Exception ignored){}
        return getName();
    }

    public LiteralArgumentBuilder<QuiltClientCommandSource> buildCommand(){
        LiteralArgumentBuilder<QuiltClientCommandSource> builder = ClientCommandManager.literal(getName());
        for(Option<?> o:getOptions()){
            o.getCommand(builder);
        }
        builder.executes(context -> {
            StringBuilder string = new StringBuilder();
            for (Option<?> o : getOptions()) {
                string.append("    ").append(Formatting.AQUA).append(o.getName()).append(": ").append(o.get()).append("\n");
            }
            ConfigUtils.sendChatMessage(Text.literal(Formatting.BLUE + "Values in this category are: \n" + string));
            return Command.SINGLE_SUCCESS;
        });
        return builder;
    }

    @Override
    public ClickableWidget getWidget(int x, int y, int width, int height) {
        return new CategoryWidget(this, x, y, width, height);
    }
}
