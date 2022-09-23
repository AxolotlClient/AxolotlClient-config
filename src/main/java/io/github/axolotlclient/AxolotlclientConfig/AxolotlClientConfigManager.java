package io.github.axolotlclient.AxolotlclientConfig;

import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlclientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AxolotlClientConfigManager {
    private static final HashMap<String, ConfigHolder> configs = new HashMap<>();
    private static final HashMap<String, ConfigManager> managers = new HashMap<>();

    public static Logger LOGGER = LogManager.getLogger("AxolotlClient Config");

    public static void registerConfig(String modid, ConfigHolder config, ConfigManager manager){
        configs.put(modid, config);
        managers.put(modid, manager);
    }

    public static void registerConfig(String modid, ConfigHolder config){
        registerConfig(modid, config, new DefaultConfigManager(modid));
    }

    public static void openConfigScreen(String modid){
       MinecraftClient.getInstance().openScreen(new OptionsScreenBuilder(MinecraftClient.getInstance().currentScreen, new OptionCategory(modid+"Config", false).addSubCategories(configs.get(modid).getCategories()), modid));
    }

    public static ConfigHolder getModConfig(String modid){
        if(modid.equals("axolotlclientconfig")){
            return new ConfigHolder() {
                @Override
                public List<OptionCategory> getCategories() {
                    return new ArrayList<>();
                }
            };
        }
        return configs.get(modid);
    }

    public static void save(String modid) {
        if(modid.equals("axolotlclientconfig")){
            return;
        }
        managers.get(modid).save();
    }

    public static void load(String modid) {
        managers.get(modid).load();
    }

    public static void saveCurrentConfig(){
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
            save(((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).modid);
        }
    }
}
