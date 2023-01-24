package io.github.axolotlclient.AxolotlClientConfig;

import io.github.axolotlclient.AxolotlClientConfig.common.ConfigHolder;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.Translations;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class AxolotlClientConfigManager extends io.github.axolotlclient.AxolotlClientConfig.common.AxolotlClientConfigManager {
    private static final HashMap<String, ConfigHolder> configs = new HashMap<>();
    private static final HashMap<String, ConfigManager> managers = new HashMap<>();
    private static final HashMap<String, List<String>> ignoredNames = new HashMap<>();

    public static Logger LOGGER = LogManager.getLogger("AxolotlClient Config");

    private static final AxolotlClientConfigManager Instance = new AxolotlClientConfigManager();

    public static AxolotlClientConfigManager getInstance(){
        return Instance;
    }

    @ApiStatus.Internal
    public static final String MODID = "axolotlclientconfig";

    /**
     * Call one of these two methods when registering a mod config.
     * @param modid your modid.
     * @param config an instance of your config class, extending ConfigHolder.
     * @param manager Your config manager. Can be omitted or null when the {@link DefaultConfigManager} should be used.
     */
    public void registerConfig(String modid, ConfigHolder config, @Nullable ConfigManager manager){
        configs.put(modid, config);

        if(manager == null){
            manager = new DefaultConfigManager(modid);
        }

        managers.put(modid, manager);

        manager.load();
    }

    public void registerConfig(String modid, ConfigHolder config){
        registerConfig(modid, config, null);
    }

    /**
     * Opens a config screen for the provided modid.
     * @param modid the modid the config screen should be opened for.
     */
    public void openConfigScreen(String modid){
       MinecraftClient.getInstance().setScreen(getConfigScreen(modid, MinecraftClient.getInstance().currentScreen));
    }

	/**
	 * Gets the built config screen for a mod.
	 * @param modid the mod of which the config screen should be built
	 * @param parent the parent screen
	 * @return the built screen
	 */
	public Screen getConfigScreen(String modid, Screen parent){
		return new OptionsScreenBuilder(parent,
                (OptionCategory) (configs.get(modid).getCategories().size() == 1 ? configs.get(modid).getCategories().get(0) :
                                        new OptionCategory(modid, false).addSubCategories(configs.get(modid).getCategories())), modid);
	}

    @ApiStatus.Internal
    public ConfigHolder getModConfig(String modid){
        if(modid.equals(MODID)){
            return ConfigHolder.EMPTY;
        }
        return configs.get(modid);
    }

    /**
     * Add a string to the list of ignored options for a specific mod
     * @param modid The mod's id.
     * @param id The option's (untranslated) name.
     */
    public void addIgnoredName(String modid, String id){
        ignoredNames.computeIfAbsent(modid, k -> new ArrayList<>()).add(id);
    }

    @ApiStatus.Internal
    public List<String> getIgnoredNames(String modid){
        return ignoredNames.computeIfAbsent(modid, k -> new ArrayList<>());
    }

    /**
     * Save the config for the provided modid
     * @param modid The modid whichs config should be saved.
     */

    public void save(String modid) {
        if(modid.equals(MODID)){
            return;
        }
        managers.get(modid).save();
    }

    /**
     * Load a config into their options.
     * @param modid The modid whichs config should be loaded.
     */

    public void load(String modid) {
        managers.get(modid).load();
    }

    @ApiStatus.Internal
    public void saveCurrentConfig(){
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
            save(((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).modid);
        }
    }

    @ApiStatus.Internal
    public HashMap<String, ConfigHolder> getConfigs(){
        return configs;
    }

    @Override
    public Translations getTranslations() {
        return TranslationProvider.getInstance();
    }
}
