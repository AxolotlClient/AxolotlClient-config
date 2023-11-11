package io.github.axolotlclient.AxolotlClientConfig.example;

import java.util.function.Function;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
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

		AxolotlClientConfig.getInstance().register(new JsonConfigManager(QuiltLoader.getConfigDir().resolve(modid + ".json"), example));

		KeyBind bind = new KeyBind(modid, InputUtil.KEY_O_CODE, modid);

		ClientTickEvents.END.register(client -> {
			if (bind.wasPressed()) {
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
	}

	public Function<Screen, ? extends Screen> getConfigScreenFactory(String name) {
		ConfigManager manager = AxolotlClientConfig.getInstance().getConfigManager(name);
		return parent -> (Screen) ConfigUI.getInstance().getScreen(this.getClass().getClassLoader(),
			manager, manager.getRoot(), parent);
	}


}
