package io.github.axolotlclient.AxolotlClientConfig.example;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.*;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBind;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public class Example implements ClientModInitializer {

	@Getter
	private static Example Instance;

	//public GraphicsOption graphicsOption;

	@Override
	public void onInitializeClient(ModContainer mod) {
		Instance = this;
		final String modid = "axolotlclientconfig-test";

		OptionCategory example = OptionCategory.create(modid);
		example.add(new BooleanOption("boolean", true));
		example.add(new BooleanOption("false", false));

		OptionCategory subCategory = OptionCategory.create("sub-category");
		subCategory.add(new BooleanOption("some option", true));
		example.add(subCategory);
		OptionCategory sub2 = OptionCategory.create("sub2");
		example.add(sub2);
		example.add(OptionCategory.create("sub3"));

		example.add(new IntegerOption("integer", 5, 0, 8));
		example.add(new FloatOption("float", 3f, 1f, 5f));
		example.add(new DoubleOption("double", 8d, 5d, 12d));
		example.add(new ColorOption("color", Colors.GREEN));

		example.add(new StringOption("string option", "default value"));
		example.add(new BooleanOption("option", false));
		example.add(new BooleanOption("other option", true));
		example.add(new GraphicsOption("graphics", 40, 40));

		AxolotlClientConfig.getInstance().register(new JsonConfigManager(QuiltLoader.getConfigDir().resolve(modid+".json"), example));

		KeyBind bind = new KeyBind(modid, InputUtil.KEY_O_CODE, modid);

		ClientTickEvents.END.register(client -> {
			if (bind.wasPressed()){
				client.setScreen(getConfigScreenFactory(modid).apply(client.currentScreen));
			}
		});

		ConfigUI.getInstance().runWhenLoaded(() -> {
			StringArrayOption option;
			example.add(option = new StringArrayOption("style",
				ConfigUI.getInstance().getStyleNames().toArray(new String[0]),
				ConfigUI.getInstance().getCurrentStyle().getName(), s -> {
				ConfigUI.getInstance().setStyle(s);
				MinecraftClient.getInstance().currentScreen.closeScreen();
			}));
			AxolotlClientConfig.getInstance().getConfigManager(modid).load();
			ConfigUI.getInstance().setStyle(option.get());
		});

		/*OptionCategory example = new OptionCategory(modid);
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
		OptionCategory sub = new OptionCategory("example_sub");
		sub.add(new BooleanOption("example_toggle", true),
			new ColorOption("example_color", Color.parse("#FF550055")),
			new StringOption("example_string", "Example §bString"),
			new BooleanOption("Very_Very_Very_Long_Snake_Case_Named_Option", false),
			new KeyBindOption("example_keybind", InputUtil.KEY_P_CODE, (binding) -> {
				if (!binding.isDefault()) {
					MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("You pressed the example keybind. Congrats!"));
				}
			}),
			new KeyBindOption("example_keybind_conflict", InputUtil.KEY_O_CODE, (binding) -> {
				MinecraftClient.getInstance().setScreen(AxolotlClientConfigManager.getInstance().getConfigScreen(modid, MinecraftClient.getInstance().currentScreen));
			}), new BooleanOption("enabled", false));
		example.add(sub);

		AxolotlClientConfigManager.getInstance().registerConfig(modid, new ConfigHolder() {
			@Override
			public List<io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory> getCategories() {
				return Collections.singletonList(example);
			}
		});*/

	}

	public Function<Screen, ? extends Screen> getConfigScreenFactory(String name) {

		return parent -> {
			try {
				return (Screen) ConfigUI.getInstance().getScreen(this.getClass().getClassLoader())
					.getConstructor(Screen.class, OptionCategory.class, String.class)
					.newInstance(parent, AxolotlClientConfig.getInstance().getConfigManager(name).getRoot(), name);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
					 NoSuchMethodException e) {
				throw new IllegalStateException(e);
			}
		};
	}


}
