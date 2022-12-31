package io.github.axolotlclient.AxolotlClientConfig.options;

import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.CategoryWidget;
import io.github.axolotlclient.AxolotlClientConfig.util.ConfigUtils;
import io.github.moehreag.clientcommands.ClientCommandRegistry;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.*;

public class OptionCategory extends io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory implements Tooltippable, WidgetSupplier {

    public OptionCategory(String key) {
        super(key);
    }

    public OptionCategory(String key, boolean registerCommand) {
        super(key, registerCommand);
    }

    @Override
    public void registerCommand() {
        ClientCommandRegistry.getInstance().registerCommand(getName().toLowerCase(Locale.ENGLISH), this::getCommandSuggestions, this::onCommandExec);
    }

    private void onCommandExec(String[] args) {
        if(args.length>0 && !args[0].equals(getName())){
            for(io.github.axolotlclient.AxolotlClientConfig.common.options.Option<?> o:getOptions()){
                if(o.getName().equals(args[0])){
                    if(args.length>1) {
                        ((Option<?>)o).onCommandExec(ConfigUtils.copyArrayWithoutFirstEntry(args));
                    } else {
                        ((Option<?>)o).onCommandExec(new String[]{});
                    }
                }
            }
        } else {
            StringBuilder builder = new StringBuilder();
            for (io.github.axolotlclient.AxolotlClientConfig.common.options.Option<?> o : getOptions()) {
                builder.append("    ").append(Formatting.AQUA).append(o.getName()).append(": ").append(Formatting.RESET).append(o.get()).append("\n");
            }
            ConfigUtils.sendChatMessage(new LiteralText(Formatting.BLUE + "Values in this category are: \n" + builder));
        }
    }

    protected List<String> getCommandSuggestions(String[] args){
        System.out.println(Arrays.toString(args));
        System.out.println(args.length);
        List<String> list = new ArrayList<>();
        if(args.length>1){
            for(io.github.axolotlclient.AxolotlClientConfig.common.options.Option<?> o:getOptions()){
                if(o.getName().equals(args[0])){
                    list.addAll(((Option<?>)o).getCommandSuggestions(ConfigUtils.copyArrayWithoutFirstEntry(ConfigUtils.copyArrayWithoutFirstEntry(args))));
                }
            }
        } else {
            for (io.github.axolotlclient.AxolotlClientConfig.common.options.Option<?> o : getOptions()) {
                list.add(o.getName());
            }
        }
        return list;
    }

    public ButtonWidget getWidget(int x, int y, int width, int height){
        return new CategoryWidget(this, x, y, width, height);
    }
}
