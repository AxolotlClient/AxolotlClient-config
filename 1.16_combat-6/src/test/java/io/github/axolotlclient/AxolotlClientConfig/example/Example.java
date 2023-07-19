package io.github.axolotlclient.AxolotlClientConfig.example;

import java.util.Collections;
import java.util.List;

import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigHolder;
import io.github.axolotlclient.AxolotlClientConfig.options.*;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class Example implements ClientModInitializer {

    private static Example Instance;

    public GraphicsOption graphicsOption;

    public static Example getInstance() {
        return Instance;
    }

    @Override
    public void onInitializeClient() {
        Instance = this;
        final String modid = "axolotlclientconfig-test";

        OptionCategory example = new OptionCategory(modid);
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
                        MinecraftClient.getInstance().openScreen(new OptionsScreen(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance().options))),
                disabledExample,
                ignored,
                graphicsOption);
        OptionCategory sub = new OptionCategory("example_sub");
        sub.add(new BooleanOption("example_toggle", true),
                new ColorOption("example_color", Color.parse("#FF550055")),
                new StringOption("example_string", "Example §bString"),
                new BooleanOption("Very_Very_Very_Long_Snake_Case_Named_Option", false),
                new KeyBindOption("example_keybind", GLFW.GLFW_KEY_PRINT_SCREEN, (binding) -> {
                    if (!binding.isDefault()) {
                        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("You pressed the example keybind. Congrats!"));
                    }
                }),
                new KeyBindOption("example_keybind_conflict", GLFW.GLFW_KEY_P, (binding) -> {
                    AxolotlClientConfigManager.getInstance().openConfigScreen(modid);
                }));
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
