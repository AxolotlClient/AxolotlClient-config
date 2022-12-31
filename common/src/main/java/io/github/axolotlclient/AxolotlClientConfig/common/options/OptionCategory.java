package io.github.axolotlclient.AxolotlClientConfig.common.options;

import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;

import java.util.*;

public abstract class OptionCategory implements Tooltippable {

    private final String name;
    private final List<Option<?>> options = new ArrayList<>();
    private final List<OptionCategory> subCategories = new ArrayList<>();

    public OptionCategory(String key){
        this(key, true);
    }

    public OptionCategory(String key, boolean registerCommand){
        this.name = key;
        if(registerCommand) {
            registerCommand();
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

    public abstract void registerCommand();
}
