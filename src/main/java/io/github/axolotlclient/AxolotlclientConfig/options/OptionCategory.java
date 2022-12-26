package io.github.axolotlclient.AxolotlclientConfig.options;

import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.CategoryWidget;
import io.github.axolotlclient.AxolotlclientConfig.util.ConfigUtils;
import io.github.moehreag.clientcommands.ClientCommandRegistry;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.*;

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
            ClientCommandRegistry.getInstance().registerCommand(name.toLowerCase(Locale.ENGLISH), this::getCommandSuggestions, this::onCommandExec);
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

    private void onCommandExec(String[] args) {
        if(args.length>0 && !args[0].equals(getName())){
            for(Option<?> o:getOptions()){
                if(o.getName().equals(args[0])){
                    if(args.length>1) {
                        o.onCommandExec(ConfigUtils.copyArrayWithoutFirstEntry(args));
                    } else {
                        o.onCommandExec(new String[]{});
                    }
                }
            }
        } else {
            StringBuilder builder = new StringBuilder();
            for (Option<?> o : getOptions()) {
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
            for(Option<?> o:getOptions()){
                if(o.getName().equals(args[0])){
                    list.addAll(o.getCommandSuggestions(ConfigUtils.copyArrayWithoutFirstEntry(ConfigUtils.copyArrayWithoutFirstEntry(args))));
                }
            }
        } else {
            for (Option<?> o : getOptions()) {
                list.add(o.getName());
            }
        }
        return list;
    }

    public ButtonWidget getWidget(int x, int y, int width, int height){
        return new CategoryWidget(this, x, y, width, height);
    }
}
