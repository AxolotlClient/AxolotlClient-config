package io.github.axolotlclient.AxolotlClientConfig.options;

import io.github.axolotlclient.AxolotlClientConfig.common.commands.CommandResponse;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.GenericOptionWidget;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.Collections;
import java.util.List;

public class GenericOption extends NoSaveOption<GenericOption.OnClick> {
    private final String labelKey;

    public GenericOption(String name, String labelKey, OnClick def) {
        super(name, def);
        this.labelKey = labelKey;
    }

    public GenericOption(String name, String labelKey, String tooltipKeyPrefix, OnClick def) {
        super(name, tooltipKeyPrefix, def);
        this.labelKey = labelKey;
    }

    public String getLabel(){
        return translate(labelKey);
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
