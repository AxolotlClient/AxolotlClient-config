package io.github.axolotlclient.AxolotlClientConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * A default config manager to use by mods not implementing a custom one.
 * The modid (all lowercase) is used for the file name, in json format.
 */

public class DefaultConfigManager implements ConfigManager {
    private final String modid;
    private final Path confPath;
    private final List<OptionCategory> modConfig;

    protected static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DefaultConfigManager(String modid){
        this(modid, modid+".json");

    }

    public DefaultConfigManager(String modid, String configFileName){
        this(modid, FabricLoader.getInstance().getConfigDir().resolve(configFileName));
    }

    public DefaultConfigManager(String modid, Path confPath){
        this(modid, confPath, AxolotlClientConfigManager.getInstance().getModConfig(modid).getCategories());
    }

    public DefaultConfigManager(String modid, Path confPath, List<OptionCategory> modConfig){
        this.modid = modid;
        this.confPath = confPath;
        this.modConfig = modConfig;
    }

    public void save(){
        try{
            saveFile();
        } catch (IOException e) {
            AxolotlClientConfigManager.LOGGER.error("Failed to save config for mod: {}!", modid);
        }
    }

    private void saveFile() throws IOException {

        JsonObject config;
        try {
            config = new JsonParser().parse(new FileReader(confPath.toString())).getAsJsonObject();
        } catch (Exception e){
            config = new JsonObject();
        }
        for(OptionCategory category : modConfig) {
            JsonObject o;
            if(config.has(getName(category)) && config.get(getName(category)).isJsonObject()) {
                o = config.get(getName(category)).getAsJsonObject();
            } else {
                o = new JsonObject();
            }
            config.add(getName(category), getConfig(o, category));
        }

        Files.write(confPath, Collections.singleton(gson.toJson(config)));
    }

    public void load() {

        loadDefaults();

        try {
            JsonObject config = new JsonParser().parse(new FileReader(confPath.toString())).getAsJsonObject();

            for(OptionCategory category : modConfig) {
                if(config.has(getName(category))) {
                    setOptions(config.get(getName(category)).getAsJsonObject(), category);
                }
            }
        } catch (Exception e){
            AxolotlClientConfigManager.LOGGER.error("Failed to load config for modid {}! Using default values...", modid);
        }
    }

    public void loadDefaults(){
        modConfig.forEach(this::setOptionDefaults);
    }
}
