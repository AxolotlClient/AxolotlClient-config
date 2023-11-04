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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBind;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public class Example implements ClientModInitializer {

	private static Example Instance;

	public String modid;

	public static Example getInstance() {
		return Instance;
	}

	@Override
	public void onInitializeClient(ModContainer container) {
		Instance = this;
		modid = "axolotlclientconfig-test";

		OptionCategory example = OptionCategory.create(modid);

		example.add(new BooleanOption("test", false));

		BooleanOption ignored = new BooleanOption("ignored_option", false);
		example.add(ignored);


		OptionCategory subCategory = OptionCategory.create("sub-category");
		subCategory.add(new BooleanOption("some option", true));
		example.add(subCategory);

		example.add(new IntegerOption("integer", 5, 0, 8));
		example.add(new FloatOption("float", 3f, 1f, 5f));
		example.add(new DoubleOption("double", 8d, 5d, 12d));
		example.add(new ColorOption("color", Colors.GREEN));

		example.add(new StringOption("string option", "default value"));
		example.add(new BooleanOption("option", false));
		example.add(new BooleanOption("other option", true));

		KeyBind binding = new KeyBind("test", InputUtil.KEY_O_CODE, "test");
		ClientTickEvents.END.register(client -> {
			if (binding.wasPressed()){
				System.out.println("Opening Screen....");
				MinecraftClient.getInstance().setScreen(getConfigScreenFactory().apply(MinecraftClient.getInstance().currentScreen));
			}
		});

		AxolotlClientConfig.getInstance().register(new JsonConfigManager(QuiltLoader.getConfigDir().resolve(modid+".json"), example));

		ConfigUI.getInstance().runWhenLoaded(() -> {
			StringArrayOption option;
			example.add(option = new StringArrayOption("style",
				ConfigUI.getInstance().getStyleNames().toArray(new String[0]),
				ConfigUI.getInstance().getCurrentStyle().getName(), s -> {
				ConfigUI.getInstance().setStyle(s);
				MinecraftClient.getInstance().setScreen(null);
			}));
			AxolotlClientConfig.getInstance().getConfigManager(modid).load();
			ConfigUI.getInstance().setStyle(option.get());
		});
	}

	public Function<Screen, ? extends Screen> getConfigScreenFactory() {

		return parent -> {
			try {
				return (Screen) Class.forName(ConfigUI.getInstance().getCurrentStyle().getScreen())
					.getConstructor(Screen.class, OptionCategory.class, String.class).newInstance(parent, AxolotlClientConfig.getInstance().getConfigManager(Example.getInstance().modid).getRoot(), modid);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
					 NoSuchMethodException | ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		};
	}


}
