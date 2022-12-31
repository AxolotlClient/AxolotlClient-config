package io.github.axolotlclient.AxolotlClientConfig.example;

import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class ExampleModMenu implements ModMenuApi {
    @Override
    public String getModId() {
        return "axolotlclientconfig-test";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return parent -> AxolotlClientConfigManager.getInstance().getConfigScreen(this.getModId(), parent);
    }
}
