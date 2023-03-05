package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.screen.overlay.Overlay;
import net.minecraft.client.MinecraftClient;

public interface OptionWidget {

    default boolean canHover(){
        return !(MinecraftClient.getInstance().currentScreen instanceof Overlay);
    }

    default void unfocus(){}
}
