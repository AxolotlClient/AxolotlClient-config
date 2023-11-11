package io.github.axolotlclient.AxolotlClientConfig.example;

import com.terraformersmc.modmenu.api.ModMenuApi;

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
