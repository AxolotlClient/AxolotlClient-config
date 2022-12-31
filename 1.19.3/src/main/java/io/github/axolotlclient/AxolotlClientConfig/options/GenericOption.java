package io.github.axolotlclient.AxolotlClientConfig.options;

import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.GenericOptionWidget;
import io.github.axolotlclient.AxolotlClientConfig.common.commands.CommandResponse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;

public class GenericOption extends NoSaveOption<GenericOption.OnClick> {
    private final String label;

    public GenericOption(String name, String label, OnClick onClick) {
        super(name, onClick);
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

    @Override
    protected CommandResponse onCommandExecution(String args) {
        get().onClick((int) MinecraftClient.getInstance().mouse.getX(), (int) MinecraftClient.getInstance().mouse.getY());
        return new CommandResponse(false, "");
    }

    public interface OnClick {
         void onClick(int mouseX, int mouseY);
    }

    @Override
    public ClickableWidget getWidget(int x, int y, int width, int height) {
        return new GenericOptionWidget(x, y, width, height, this);
    }
}