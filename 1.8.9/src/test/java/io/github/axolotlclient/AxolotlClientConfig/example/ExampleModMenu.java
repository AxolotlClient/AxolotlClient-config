package io.github.axolotlclient.AxolotlClientConfig.example;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ConfigUI;
import net.minecraft.client.gui.screen.Screen;

public class ExampleModMenu implements ModMenuApi {
    /*@Override
    public String getModId() {
        return "axolotlclientconfig-test";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {

        return parent -> {
			try {
				return (Screen) Class.forName(ConfigUI.getInstance().getCurrentStyle().getScreen())
					.getConstructor(Screen.class, OptionCategory.class).newInstance(parent, AxolotlClientConfig.getInstance().getConfigManager(Example.getInstance().modid).getRoot());
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
					 NoSuchMethodException | ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		};
    }*/
}
