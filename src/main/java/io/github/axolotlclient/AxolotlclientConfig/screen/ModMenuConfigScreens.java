package io.github.axolotlclient.AxolotlclientConfig.screen;

import com.mojang.blaze3d.platform.InputUtil;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.option.KeyBind;
import net.minecraft.text.Text;
import org.quiltmc.loader.api.QuiltLoader;

import java.util.HashMap;
import java.util.Map;

public class ModMenuConfigScreens implements ModMenuApi {

    private static final OptionCategory example = new OptionCategory("example_config", QuiltLoader.isDevelopmentEnvironment());

    public ModMenuConfigScreens(){
        if(example.getOptions().isEmpty()){
            BooleanOption ignored = new BooleanOption("ignored_option", false);
            AxolotlClientConfigManager.addIgnoredName(AxolotlClientConfigManager.MODID, ignored.getName());
            BooleanOption disabledExample = new BooleanOption("example_toggle_disabled", true);
            disabledExample.setForceOff(true, "Example Reason");
            example.add(new BooleanOption("example_toggle", false),
                    new DoubleOption("example_slider", 5D, 0, 10),
                    new EnumOption("example_enum_option", new String[]{"example_enum_option_1", "example_enum_option_2", "example_enum_option_3"}, "example_enum_option_2"),
                    new ColorOption("example_color", -162555),
                    new StringOption("example_string", "Example §2String"),
                    new GenericOption("example_generic", "Open Minecraft Options", (mouseX, mouseY)->
                            MinecraftClient.getInstance().setScreen(new OptionsScreen(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance().options))),
                    disabledExample,
                    ignored);
            OptionCategory sub = new OptionCategory("example_sub", QuiltLoader.isDevelopmentEnvironment());
            sub.add(new BooleanOption("example_toggle", true),
                    new ColorOption("example_color", Color.parse("#FF550055")),
                    new StringOption("example_string", "Example §bString"),
                    new BooleanOption("Very_Very_Very_Long_Snake_Case_Named_Option", false),
                    new KeyBindOption("example_keybind", new KeyBind("exampleKey", InputUtil.KEY_PRINT_SCREEN_CODE, "example.category"), (binding)->{
                        if (!binding.isDefault()){
                            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("You pressed the example keybind. Congrats!"));
                        }
                    }),
                    new KeyBindOption("example_keybind_conflict", new KeyBind("exampleKey", InputUtil.KEY_PRINT_SCREEN_CODE, "example.category"), (binding)->{}));
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
