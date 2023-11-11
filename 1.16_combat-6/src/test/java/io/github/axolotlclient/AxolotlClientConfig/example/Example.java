package io.github.axolotlclient.AxolotlClientConfig.example;

import java.util.function.Function;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.*;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;

public class Example implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		String modid = "axolotlclientconfig-test";

		OptionCategory example = OptionCategory.create(modid);

		example.add(new BooleanOption("test", false));

		BooleanOption ignored = new BooleanOption("ignored_option", false);
		example.add(ignored);


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

		KeyBinding binding = new KeyBinding("test", -1, "test");
		KeyBindingHelper.registerKeyBinding(binding);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (binding.wasPressed()) {
				System.out.println("Opening Screen....");
				MinecraftClient.getInstance().openScreen(getConfigScreenFactory(modid).apply(MinecraftClient.getInstance().currentScreen));
			}
		});

		AxolotlClientConfig.getInstance().register(new JsonConfigManager(FabricLoader.getInstance().getConfigDir().resolve(modid + ".json"), example));

		ConfigUI.getInstance().runWhenLoaded(() -> {
			StringArrayOption option;
			example.add(option = new StringArrayOption("style",
				ConfigUI.getInstance().getStyleNames().toArray(new String[0]),
				ConfigUI.getInstance().getCurrentStyle().getName(), s -> {
				ConfigUI.getInstance().setStyle(s);
				MinecraftClient.getInstance().openScreen(null);
			}));
			AxolotlClientConfig.getInstance().getConfigManager(modid).load();
			ConfigUI.getInstance().setStyle(option.get());
		});
	}

	public Function<Screen, ? extends Screen> getConfigScreenFactory(String name) {
		ConfigManager manager = AxolotlClientConfig.getInstance().getConfigManager(name);
		return parent -> (Screen) ConfigUI.getInstance().getScreen(this.getClass().getClassLoader(),
			manager, manager.getRoot(), parent);
	}


}
