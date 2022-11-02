package io.github.axolotlclient.AxolotlclientConfig.screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.OptionsScreen;

import java.util.HashMap;
import java.util.Map;

public class ModMenuConfigScreens implements ModMenuApi {

    private static final OptionCategory example = new OptionCategory("Example Config", false);

    public ModMenuConfigScreens(){
        if(example.getOptions().isEmpty()){
            BooleanOption disabledExample = new BooleanOption("Disabled Example Toggle", true);
            disabledExample.setForceOff(true, "Example Reason");
            example.add(new BooleanOption("Example Toggle", false),
                    new DoubleOption("Example Slider", 5D, 0, 10),
                    new EnumOption("Example Enum Option", new String[]{"Option 1", "Option 2", "Option 3"}, "Option 1"),
                    new ColorOption("Example Color Option", -162555),
                    new StringOption("Example String Option", "Example §2String"),
                    new GenericOption("Example Generic Option", "Open Minecraft Options", (mouseX, mouseY)->{
                        MinecraftClient.getInstance().setScreen(new OptionsScreen(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance().options));
                    }),
                    disabledExample);
            OptionCategory sub = new OptionCategory("Example Sub Category", false);
            sub.add(new BooleanOption("Example Toggle", true),
                    new ColorOption("Example Color Option", Color.parse("#FF550055")),
                    new StringOption("Example String Option", "Example §bString"));
            example.addSubCategory(sub);
        }
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> new OptionsScreenBuilder(parent, example, "axolotlclientconfig");
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        HashMap<String, ConfigScreenFactory<?>> factories = new HashMap<>();
        AxolotlClientConfigManager.getConfigs().forEach((s, configHolder) ->
                factories.put(s, (parent) ->
                        new OptionsScreenBuilder(parent, new OptionCategory(s +" Config", false)
                                .addSubCategories(AxolotlClientConfigManager.getModConfig(s).getCategories()), s)));
        return factories;
    }
}
