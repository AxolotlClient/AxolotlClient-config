package io.github.axolotlclient.AxolotlclientConfig;

import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlclientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.loader.api.QuiltLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AxolotlClientConfigManager {
    private static final HashMap<String, ConfigHolder> configs = new HashMap<>();
    private static final HashMap<String, ConfigManager> managers = new HashMap<>();
    private static final HashMap<String, List<String>> ignoredNames = new HashMap<>();

    public static Logger LOGGER = LogManager.getLogger("AxolotlClient Config");

    private static final String MODID = "axolotlclientconfig";

    static {
        ignoredNames.put(MODID, new ArrayList<>());
    }

    /**
     * Call one of these two methods when registering a mod config.
     * @param modid your modid.
     * @param config an instance of your config class, extending ConfigHolder.
     * @param manager Your config manager. Can be omitted when the {@see DefaultConfigManager} should be used.
     */
    public static void registerConfig(String modid, ConfigHolder config, ConfigManager manager){
        configs.put(modid, config);
        managers.put(modid, manager);

        try {
            QuiltLoader.getModContainer("");
        } catch (NoClassDefFoundError e){
            AxolotlClientConfigManager.LOGGER.warn("Running under Fabric, Commands for mod {} will not work!", modid);
        }

        manager.load();
    }

    public static void registerConfig(String modid, ConfigHolder config){
        registerConfig(modid, config, new DefaultConfigManager(modid));
    }

    /**
     * Opens a config screen for the provided modid.
     * @param modid the modid the config screen should be opened for.
     */
    public static void openConfigScreen(String modid){
       MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(MinecraftClient.getInstance().currentScreen, new OptionCategory(modid+" Config", false).addSubCategories(configs.get(modid).getCategories()), modid));
    }

    @ApiStatus.Internal
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

    /**
     * Add a string to the list of ignored options for a specific mod
     * @param modid The mod's id.
     * @param id The option's (untranslated) name.
     */
    public static void addIgnoredName(String modid, String id){
        ignoredNames.get(modid).add(id);
    }

    @ApiStatus.Internal
    public static List<String> getIgnoredNames(String modid){
        return ignoredNames.computeIfAbsent(modid, k -> new ArrayList<>());
    }

    /**
     * Save the config for the provided modid
     * @param modid The modid whichs config should be saved.
     */

    public static void save(String modid) {
        if(modid.equals(MODID)){
            return;
        }
        managers.get(modid).save();
    }

    /**
     * Load a config into their options.
     * @param modid The modid whichs config should be loaded.
     */

    public static void load(String modid) {
        managers.get(modid).load();
    }

    @ApiStatus.Internal
    public static void saveCurrentConfig(){
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
            save(((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).modid);
        }
    }

    @ApiStatus.Internal
    public static HashMap<String, ConfigHolder> getConfigs(){
        return configs;
    }
}
