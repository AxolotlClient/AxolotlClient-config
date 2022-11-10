package io.github.axolotlclient.AxolotlclientConfig.screen;

import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModMenuScreens implements ModMenuApi {

    private static final OptionCategory example = new OptionCategory("Example Config", false);

    public ModMenuScreens(){
        if(example.getOptions().isEmpty()){
            BooleanOption ignored = new BooleanOption("ignored_option", false);
            AxolotlClientConfigManager.addIgnoredName(AxolotlClientConfigManager.MODID, ignored.getName());
            BooleanOption disabledExample = new BooleanOption("Disabled Example Toggle", true);
            disabledExample.setForceOff(true, "Example Reason");
            example.add(new BooleanOption("Example Toggle", false),
                    new DoubleOption("Example Slider", 5D, 0, 10),
                    new EnumOption("Example Enum Option", new String[]{"Option 1", "Option 2", "Option 3"}, "Option 2"),
                    new ColorOption("Example Color Option", -162555),
                    new StringOption("Example String Option", "Example §2String"),
                    new GenericOption("Example Generic Option", "Open Minecraft Options", (mouseX, mouseY)->{
                        MinecraftClient.getInstance().openScreen(new SettingsScreen(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance().options));
                    }),
                    disabledExample,
                    ignored);
            OptionCategory sub = new OptionCategory("Example Sub Category", false);
            sub.add(new BooleanOption("Example Toggle", true),
                    new ColorOption("Example Color Option", Color.parse("#FF550055")),
                    new StringOption("Example String Option", "Example §bString"),
                    new BooleanOption("Very_Very_Very_Long_Snake_Case_Named_Option", false));
            example.addSubCategory(sub);
        }
    }

    @Override
    public String getModId() {
        return "axolotlclientconfig";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return (parent) -> new OptionsScreenBuilder(parent, example, "axolotlclientconfig");
    }

    /*@Override // For when 1.8.9 has a modmenu version that supports crazy shit like this
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        HashMap<String, ConfigScreenFactory<?>> factories = new HashMap<>();
        AxolotlClientConfigManager.getConfigs().forEach((s, configHolder) ->
                factories.put(s, (parent) ->
                        new OptionsScreenBuilder(parent, new OptionCategory(s +" Config", false)
                                .addSubCategories(AxolotlClientConfigManager.getModConfig(s).getCategories()), s)));
        return factories;
    }*/
}
