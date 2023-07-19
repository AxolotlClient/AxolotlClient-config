package io.github.axolotlclient.AxolotlClientConfig.example;

import java.io.FileReader;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.DefaultConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigHolder;
import io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.options.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.option.KeyBind;
import net.minecraft.text.Text;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class Example implements ClientModInitializer {

    private static Example Instance;

    public GraphicsOption graphicsOption;

    public static Example getInstance() {
        return Instance;
    }

    @Override
    public void onInitializeClient(ModContainer mod) {
        Instance = this;
        final String modid = "axolotlclientconfig-test";

        OptionCategory example = new io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory(modid);
        BooleanOption ignored = new BooleanOption("ignored_option", false);
        AxolotlClientConfigManager.getInstance().addIgnoredName(modid, ignored.getName());
        BooleanOption disabledExample = new BooleanOption("example_toggle_disabled", true);
        disabledExample.setForceOff(true, "Example Reason");
        graphicsOption = new GraphicsOption("example_graphic", new int[][]{
                new int[]{0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1}}, true);
        example.add(new BooleanOption("example_toggle", false),
                new DoubleOption("example_slider", 5D, 0, 10),
                new EnumOption("example_enum_option", new String[]{"example_enum_option_1", "example_enum_option_2", "example_enum_option_3"}, "example_enum_option_2"),
                new ColorOption("example_color", -162555),
                new StringOption("example_string", "Example §2String"),
                new GenericOption("example_generic", "Open Minecraft Options", (mouseX, mouseY) ->
                        MinecraftClient.getInstance().setScreen(new OptionsScreen(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance().options))),
                disabledExample,
                ignored,
                graphicsOption);
        OptionCategory sub = new io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory("example_sub");
        sub.add(new BooleanOption("example_toggle", true),
                new ColorOption("example_color", Color.parse("#FF550055")),
                new StringOption("example_string", "Example §bString"),
                new BooleanOption("Very_Very_Very_Long_Snake_Case_Named_Option", false),
                new KeyBindOption("example_keybind", new KeyBind("exampleKey", InputUtil.KEY_PRINT_SCREEN_CODE, "example.category"), (binding) -> {
                    if (!binding.isDefault()) {
                        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("You pressed the example keybind. Congrats!"));
                    }
                }),
                new KeyBindOption("example_keybind_conflict", InputUtil.KEY_O_CODE, (binding) -> {
					AxolotlClientConfigManager.getInstance().openConfigScreen(modid);
                }), new BooleanOption("enabled", false));
        example.add(sub);
		example.add(AxolotlClientConfigConfig.roundedRects);

        AxolotlClientConfigManager.getInstance().registerConfig(modid, new ConfigHolder() {
            @Override
            public List<io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory> getCategories() {
                return Collections.singletonList(example);
            }
        });

    }


}
