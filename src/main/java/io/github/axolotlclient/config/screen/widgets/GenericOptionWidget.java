package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.GenericOption;
import net.minecraft.client.gui.widget.ButtonWidget;

public class GenericOptionWidget extends ButtonWidget {

    private final GenericOption option;

    public GenericOptionWidget(int x, int y, int width, int height, GenericOption option) {
        super(0, x, y, width, height, option.getLabel());
        this.option = option;
    }

    public void onClick(int mouseX, int mouseY) {
        option.get().onClick(mouseX, mouseY);
    }
}
