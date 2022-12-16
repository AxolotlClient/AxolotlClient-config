package io.github.axolotlclient.AxolotlclientConfig.screen;

import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.text.LiteralText;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModMenuScreens implements ModMenuApi {

    private static final OptionCategory example = new OptionCategory("example_config", FabricLoader.getInstance().isDevelopmentEnvironment());

    public ModMenuScreens(){
        if(example.getOptions().isEmpty()){
            BooleanOption ignored = new BooleanOption("ignored_option", false);
            AxolotlClientConfigManager.addIgnoredName(AxolotlClientConfigManager.MODID, ignored.getName());
            BooleanOption disabledExample = new BooleanOption("example_toggle_disabled", true);
            disabledExample.setForceOff(true, "example_reason");
            example.add(new BooleanOption("example_toggle", false),
                    new DoubleOption("example_slider", 5D, 0, 10),
                    new EnumOption("example_enum_option", new String[]{"example_enum_option_1", "example_enum_option_2", "example_enum_option_3"}, "example_enum_option_2"),
                    new ColorOption("example_color", -162555),
                    new StringOption("example_string", "Example §2String"),
                    new GenericOption("example_generic", "Open Minecraft Options", (mouseX, mouseY)->
                            MinecraftClient.getInstance().openScreen(new SettingsScreen(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance().options))),
                    disabledExample,
                    ignored);
            OptionCategory sub = new OptionCategory("example_sub", FabricLoader.getInstance().isDevelopmentEnvironment());
            sub.add(new BooleanOption("example_toggle", true),
                    new ColorOption("example_color", Color.parse("#FF550055")),
                    new StringOption("example_string", "Example §bString"),
                    new BooleanOption("Very_Very_Very_Long_Snake_Case_Named_Option", false),
                    new KeyBindOption("example_keybind", new KeyBinding("exampleKey", Keyboard.KEY_SYSRQ, "example.category"), (binding)->{
                        if (binding.getCode() != binding.getDefaultCode()){
                            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("You pressed the example keybind. Congrats!"));
                        }
                    }),
                    new KeyBindOption("example_keybind_conflict", new KeyBinding("exampleKey", Keyboard.KEY_SYSRQ, "example.category"), (binding)->{}));
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
                        AxolotlClient.getConfigScreen(s, parent));
        return factories;
    }*/
}
