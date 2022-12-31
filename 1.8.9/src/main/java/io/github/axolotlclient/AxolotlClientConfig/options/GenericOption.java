package io.github.axolotlclient.AxolotlClientConfig.options;

import io.github.axolotlclient.AxolotlClientConfig.common.commands.CommandResponse;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.GenericOptionWidget;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.Collections;
import java.util.List;

public class GenericOption extends NoSaveOption<GenericOption.OnClick> {
    private final String label;

    public GenericOption(String name, String label, OnClick def) {
        super(name, def);
        this.label = label;
    }

    public GenericOption(String name, String label, String tooltipKeyPrefix, OnClick def) {
        super(name, tooltipKeyPrefix, def);
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        get().onClick(0, 0);
        return new CommandResponse(false, "");
    }

    @Override
    public List<String> getCommandSuggestions(String[] args) {
        return Collections.emptyList();
    }



    public interface OnClick {
         void onClick(int mouseX, int mouseY);
    }

    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height) {
        return new GenericOptionWidget(x, y, width, height, this);
    }
}
